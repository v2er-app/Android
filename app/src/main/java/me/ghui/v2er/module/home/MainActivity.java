package me.ghui.v2er.module.home;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import me.ghui.toolbox.android.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.OnFragmentReEnter;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.create.CreateTopicActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.star.StarActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.settings.SettingActivity;
import me.ghui.v2er.module.settings.UserManualActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.CSlidingTabLayout;
import me.ghui.v2er.widget.FollowProgressBtn;

public class MainActivity extends BaseActivity implements View.OnClickListener, UpdateUnReadMsgDelegate, CheckInContract.IView, OnTabSelectListener, HomeFilterMenu.OnMenuItemClickListener {
    private static final String CURRENT_PAGE = KEY("current_page_index");
    public static boolean isAlive;
    private final String[] TAB_TITLES = {" 全部", "消息", "节点"};
    @BindView(R.id.left_draw_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationview_main)
    NavigationView mNavigationView;
    @BindView(R.id.tablayout_main)
    CSlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    @BindView(R.id.main_toolbar)
    BaseToolBar mToolbar;
    @BindView(R.id.tab_menu_container)
    ViewGroup mTabMenuContainer;
    private NewsFragment mNewsFragment;
    private MsgFragment mMsgFragment;
    private NodesNavFragment mNavFragment;
    private View mNavHeaderView;
    private ImageView mAvatarImg;
    private TextView mUserNameTv;
    private FollowProgressBtn mCheckInBtn;
    private MenuItem mCreateMenuItem;
    private CheckInPresenter mCheckInPresenter;
    private TextView mTab1View;
    private TextView mTab2View;
    private TextView mTab3View;
    private MenuItem mNightMenuItem;
    private SwitchCompat mNightSwitch;
    private HomeFilterMenu mFilterMenu;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_main;
    }

    @Override
    protected boolean supportSlideBack() {
        return false;
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void configSystemBars(Window window) {
//        super.configSystemBars(window);
    }

    protected void configToolBar() {
        Utils.setPaddingForStatusBar(mToolbar);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setElevation(0);
        mToolbar.setNavigationIcon(R.drawable.nav);
        mToolbar.getNavigationIcon().setTint(Theme.getColor(R.attr.icon_tint_color, this));
        mToolbar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
        mToolbar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                pushFragment(SearchFragment.newInstance());
            }
            return true;
        });
    }

    @Override
    public boolean onToolbarDoubleTaped() {
        View rootView = getCurrentFragment().getView();
        if (rootView == null) return false;
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.base_recyclerview);
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        isAlive = true;
        configToolBar();
        Bus.register(this);
        mNavigationView.setItemIconTintList(null);
        mNavHeaderView = mNavigationView.getHeaderView(0);
        mAvatarImg = mNavHeaderView.findViewById(R.id.avatar_img);
        mUserNameTv = mNavHeaderView.findViewById(R.id.user_name_tv);
        mCheckInBtn = mNavHeaderView.findViewById(R.id.check_in_progress_btn);
        mAvatarImg.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        mCheckInBtn.setOnClickListener(this);
        updateHeaderView();
        mCreateMenuItem = mNavigationView.getMenu().findItem(R.id.create_nav_item);
        mCreateMenuItem.setVisible(Pref.readBool(R.string.pref_key_hide_create_btn));
        mNightMenuItem = mNavigationView.getMenu().findItem(R.id.day_night_item);
        mNightSwitch = mNightMenuItem.getActionView().findViewById(R.id.drawer_switch);
        mNightSwitch.setChecked(DayNightUtil.isNightMode());
        mNightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String dayNightTitle = getString(isChecked ? R.string.night_mode : R.string.day_mode);
            mNightMenuItem.setTitle(dayNightTitle);
            DayNightUtil.saveMode(isChecked ? DayNightUtil.NIGHT_MODE : DayNightUtil.DAY_MODE);
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra(CURRENT_PAGE, mSlidingTabLayout.getCurrentTab());
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        });

        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.hot_nav_item:
                    Navigator.from(getContext()).to(DailyHotActivity.class).start();
                    break;
                case R.id.care_nav_item:
                    Navigator.from(getContext()).to(SpecialCareActivity.class).start();
                    break;
                case R.id.star_nav_item:
                    Navigator.from(getContext()).to(StarActivity.class).start();
                    break;
                case R.id.setting_nav_item:
                    Navigator.from(getContext()).to(SettingActivity.class).start();
                    break;
                case R.id.faq_nav_item:
                    startActivity(new Intent(getContext(), UserManualActivity.class));
                    break;
                case R.id.create_nav_item:
                    if (UserUtils.notLoginAndProcessToLogin(false, getContext())) return true;
                    Navigator.from(getContext()).to(CreateTopicActivity.class).start();
                    break;
                case R.id.day_night_item:
                    mNightSwitch.toggle();
                    break;
            }
            delay(50, () -> mDrawerLayout.closeDrawer(Gravity.START, false));
            return true;
        });

        Menu menu = mNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).getIcon().setTint(Theme.getColor(R.attr.icon_tint_color, this));
        }
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                updateHeaderView();
                mCreateMenuItem.setVisible(Pref.readBool(R.string.pref_key_hide_create_btn));
            }
        });

        TAB_TITLES[0] = TabInfo.getSelectTab().title;
        mViewPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES);
        mSlidingTabLayout.setOnTabSelectListener(this);
        configNewsTabTitle();
        initCheckIn();

        int index = getIntent().getIntExtra(CURRENT_PAGE, 0);
        mSlidingTabLayout.setCurrentTab(index);
    }

    private void configNewsTabTitle() {
        mTab1View = mSlidingTabLayout.getTitleView(0);
        mTab2View = mSlidingTabLayout.getTitleView(1);
        mTab3View = mSlidingTabLayout.getTitleView(2);
        int padding = ScaleUtils.dp(6f);
        mTab1View.setPadding(mTab1View.getPaddingLeft(), padding, mTab1View.getPaddingRight(), padding);
        mTab2View.setPadding(mTab2View.getPaddingLeft(), padding, mTab2View.getPaddingRight(), padding);
        mTab3View.setPadding(mTab3View.getPaddingLeft(), padding, mTab3View.getPaddingRight(), padding);

        mTab1View.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.animate_triangle_down, 0);
        mTab1View.setCompoundDrawablePadding(ScaleUtils.dp(6));
    }

    private void initCheckIn() {
        mCheckInPresenter = new CheckInPresenter(this);
        mCheckInPresenter.start();
    }

    private void updateHeaderView() {
        UserInfo userInfo = UserUtils.getUserInfo();
        if (userInfo != null) {
            mUserNameTv.setText(userInfo.getUserName());
            GlideApp.with(getContext())
                    .load(userInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(mAvatarImg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar_img:
            case R.id.user_name_tv:
                if (UserUtils.isLogin()) {
                    UserHomeActivity.open(UserUtils.getUserInfo().getUserName(), this, null, UserUtils.getUserInfo().getAvatar());
                } else {
                    Navigator.from(this).to(LoginActivity.class).start();
                }
                mDrawerLayout.closeDrawers();
                break;
            case R.id.check_in_progress_btn:
                if (!UserUtils.isLogin()) {
                    toast("请先登录!");
                    return;
                }
                if (mCheckInBtn.isNormal()) {
                    mCheckInPresenter.checkIn(true);
                } else if (mCheckInBtn.isFinished()) {
                    toast("已连续签到" + mCheckInPresenter.checkInDays() + "天");
                } else {
                    toast("正在签到请稍后...");
                }
                break;
        }
    }

    private int getCurrentTab() {
        return mSlidingTabLayout.getCurrentTab();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (!isBackableEmpty()) {
            super.onBackPressed();
            return;
        }

        if (getCurrentTab() != 0) {
            mSlidingTabLayout.setCurrentTab(0);
            return;
        }

        if (mFilterMenu != null && mFilterMenu.isShowing()) {
            mFilterMenu.hide();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void updateUnReadMsg(int position, int count) {
        if (count <= 0) {//hide
            mSlidingTabLayout.hideMsg(position);
        } else {
            mSlidingTabLayout.showMsg(position, count);
            //config sliding msgview
            float padding = getResources().getDimension(R.dimen.mediumTextSize) / 2f;
            mSlidingTabLayout.setMsgMargin(1, padding * 0.92f, padding * 0.28f);
            MsgView msgView = mSlidingTabLayout.getMsgView(1);
            float textSize = getResources().getDimension(R.dimen.tinyTextSize);
            msgView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
            lp.width = Math.round(textSize * (count + "").length() * 1.5f);
            lp.height = Math.round(textSize * 1.5f);
            msgView.setLayoutParams(lp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        Bus.unRegister(this);
        Log.e("MainActivity", "MainActivity is destoryed");
    }

    private Fragment getCurrentFragment() {
        int pos = getCurrentTab();
        switch (pos) {
            case 0:
                return mNewsFragment;
            case 1:
                return mMsgFragment;
            case 2:
                return mNavFragment;
        }
        return null;
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (getCurrentFragment() instanceof OnFragmentReEnter) {
            ((OnFragmentReEnter) getCurrentFragment()).onFragmentReEnter();
        }
    }

    @Override
    public FollowProgressBtn checkInBtn() {
        return mCheckInBtn;
    }

    @Override
    public void onTabSelect(int position) {
        Logger.d("onTabSelect");
        if (position == 0) {
            mTab1View.getCompoundDrawables()[2].setTint(Theme.getColor(R.attr.tablayout_selected_color, this));
        } else {
            mTab1View.getCompoundDrawables()[2].setTint(Theme.getColor(R.attr.tablayout_unselected_color, this));
            if (mFilterMenu != null && mFilterMenu.isShowing()) {
                mFilterMenu.hide();
            }
        }
    }

    @Override
    public void onTabReselect(int position) {
        Logger.d("onTabReSelect");
        if (position == 0) {
            if (mFilterMenu == null) {
                mFilterMenu = new HomeFilterMenu(mTabMenuContainer, mTab1View);
                mFilterMenu.setOnItemClickListner(this);
            }
            mFilterMenu.toggle();
        }
    }

    @Override
    public void onMenuItemClicked(TabInfo tabInfo) {
        ChangeTabTypeDelegate delegate = mNewsFragment;
        delegate.changeTabType(tabInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextSizeChanged(TextSizeChangeEvent event) {
        recreate();
    }

    public interface ChangeTabTypeDelegate {
        void changeTabType(TabInfo tabInfo);
    }

    private class SlidePagerAdapter extends FragmentPagerAdapter {

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    NewsFragment newsFragment = NewsFragment.newInstance();
                    newsFragment.setUpdateUnReadMsgDelegate(MainActivity.this);
                    fragment = newsFragment;
                    break;
                case 1:
                    MsgFragment msgFragment = MsgFragment.newInstance();
                    msgFragment.setUpdateUnReadMsgDelegate(MainActivity.this);
                    fragment = msgFragment;
                    break;
                case 2:
                    fragment = NodesNavFragment.newInstance();
                    break;
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    mNewsFragment = (NewsFragment) fragment;
                    break;
                case 1:
                    mMsgFragment = (MsgFragment) fragment;
                    break;
                case 2:
                    mNavFragment = (NodesNavFragment) fragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

    ;
}
