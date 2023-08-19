package me.ghui.v2er.module.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.target.Target;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Page;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.create.CreateTopicActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.star.StarActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.settings.UserManualActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.DarkModelUtils;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Theme;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.CSlidingTabLayout;
import me.ghui.v2er.widget.FollowProgressBtn;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        UpdateUnReadMsgDelegate, CheckInContract.IView, OnTabSelectListener,
        HomeFilterMenu.OnMenuItemClickListener {

    private static final String TAB_INDEX = KEY("tab_index");
    private static final String PAGE_ONE_DATA = KEY("page_one_data");
    private static final String PAGE_TWO_DATA = KEY("page_two_data");
    private static final String PAGE_THREE_DATA = KEY("page_three_data");
    private static final String TOPIC_IS_APPBAR_EXPANDED = KEY("toolbar_is_appbar_expanded");

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
    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;
    private NewsFragment mNewsFragment;
    private MsgFragment mMsgFragment;
    private NodesNavFragment mNavFragment;
    private View mNavHeaderView;
    private ImageView mAvatarImg;
    private TextView mUserNameTv;
    private FollowProgressBtn mCheckInBtn;
    private CheckInPresenter mCheckInPresenter;
    private TextView mTab1View;
    private MenuItem mNightMenuItem;
    private SwitchCompat mNightSwitch;
    private HomeFilterMenu mFilterMenu;
    private boolean isAppbarExpanted = true;

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

    @SuppressLint({"CheckResult", "WrongConstant"})
    protected void configToolBar() {
        Utils.setPaddingForStatusBar(mAppBarLayout);
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
        RecyclerView recyclerView = rootView.findViewById(R.id.base_recyclerview);
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
            return true;
        }
        return false;
    }


    private void refreshDayNightItem() {
        mNightMenuItem.setTitle(DarkModelUtils.isAutoModeEnabled() ? "深色模式(自动)" : "深色模式");
        mNightSwitch.setChecked(DarkModelUtils.isDarkMode());
    }

    @Override
    protected void init() {
        isAlive = true;
        configToolBar();

        mNavigationView.setItemIconTintList(null);
        mNavHeaderView = mNavigationView.getHeaderView(0);
        mAvatarImg = mNavHeaderView.findViewById(R.id.avatar_img);
        mUserNameTv = mNavHeaderView.findViewById(R.id.user_name_tv);
        mCheckInBtn = mNavHeaderView.findViewById(R.id.check_in_progress_btn);
        mAvatarImg.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        mCheckInBtn.setOnClickListener(this);
        mNightMenuItem = mNavigationView.getMenu().findItem(R.id.day_night_item);

        mAvatarImg.setOnLongClickListener(v -> {
            new ConfirmDialog.Builder(getActivity())
                    .title("退出登录")
                    .msg("确定退出吗？")
                    .positiveText(R.string.ok, dialog -> {
                        UserUtils.clearLogin();
                        Navigator.from(getActivity())
                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .to(MainActivity.class).start();
                    })
                    .negativeText(R.string.cancel)
                    .build().show();
            return false;
        });

        mNightSwitch = mNightMenuItem.getActionView().findViewById(R.id.drawer_switch);
        updateDrawLayout();
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
                    Navigator.from(getContext()).to(Page.SETTING).start();
                    break;
                case R.id.faq_nav_item:
                    startActivity(new Intent(getContext(), UserManualActivity.class));
                    break;
                case R.id.create_nav_item:
                    if (UserUtils.notLoginAndProcessToLogin(false, getContext())) return true;
                    Navigator.from(getContext()).to(CreateTopicActivity.class).start();
                    break;
                case R.id.day_night_item:
                    onNightMenuItemClicked(DarkModelUtils.isDarkMode());
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
                updateDrawLayout();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                isAppbarExpanted = state == State.EXPANDED;
                int paddingTop = isAppbarExpanted ? 0 : 36;
                mSlidingTabLayout.setPadding(0, paddingTop, 0, 0);
            }
        });

        TAB_TITLES[0] = TabInfo.getSelectTab().title;
        mViewPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES);
        mSlidingTabLayout.setOnTabSelectListener(this);
        configNewsTabTitle();
        initCheckIn();

        int index = getIntent().getIntExtra(TAB_INDEX, 0);
        mSlidingTabLayout.setCurrentTab(index);
        isAppbarExpanted = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, true);
        mAppBarLayout.setExpanded(isAppbarExpanted);
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(TAB_INDEX, mSlidingTabLayout.getCurrentTab())
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanted)
                .putExtra(PAGE_ONE_DATA, mNewsFragment.getRestoreData())
                .putExtra(PAGE_TWO_DATA, mMsgFragment.getRestoreData())
                .putExtra(PAGE_THREE_DATA, mNavFragment.getRestoreData())
                .reload();
    }

    private void onNightMenuItemClicked(boolean isNightMode) {
        int wanttedMode = isNightMode ? DarkModelUtils.DEFAULT_MODE : DarkModelUtils.DARK_MODE;
        if (DarkModelUtils.isAutoModeEnabled()) {
            new ConfirmDialog.Builder(MainActivity.this)
                    .title("要关闭自动切换模式吗？")
                    .msg("当前为自动切换模式，确定关闭自动切换吗")
                    .positiveText("关闭", dialog -> {
                        DarkModelUtils.saveEnableAutoSwitch(false);
                        DarkModelUtils.saveModeMannually(wanttedMode);
                        reloadMode(wanttedMode);
                    }).negativeText("暂时不用")
                    .build().show();
        } else {
            mNightSwitch.toggle();
            DarkModelUtils.saveModeMannually(wanttedMode);
            reloadMode(wanttedMode);
        }
    }

    private void configNewsTabTitle() {
        int padding = ScaleUtils.dp(6f);
        mSlidingTabLayout.setTitleViewVerticalPadding(0, padding);
        mSlidingTabLayout.setTitleViewVerticalPadding(1, padding);
        mSlidingTabLayout.setTitleViewVerticalPadding(2, padding);
        mTab1View = mSlidingTabLayout.getTitleView(0);
        mTab1View.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.animate_triangle_down, 0);
        mTab1View.setCompoundDrawablePadding(ScaleUtils.dp(6));
    }

    private void initCheckIn() {
        mCheckInPresenter = new CheckInPresenter(this);
        mCheckInPresenter.start();
    }

    private void updateDrawLayout() {
        UserInfo userInfo = UserUtils.getUserInfo();
        if (userInfo != null) {
            mUserNameTv.setText(userInfo.getUserName());
            GlideApp.with(getContext())
                    .load(userInfo.getAvatar())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(mAvatarImg);
        } else {
            mUserNameTv.setText("请先登录");
            mAvatarImg.setImageResource(R.drawable.default_avatar_drawable);
        }
        refreshDayNightItem();
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

    @SuppressLint("WrongConstant")
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
    protected void onResume() {
        super.onResume();
        ViewUtils.configToolbarScroll(mToolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        Bus.unRegister(this);
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
    public FollowProgressBtn checkInBtn() {
        return mCheckInBtn;
    }

    @Override
    public void onTabSelect(int position) {
        L.d("onTabSelect");
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
        L.d("onTabReSelect");
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
            BaseHomeFragment.RestoreData restoreData;
            switch (position) {
                case 0:
                    restoreData = (BaseHomeFragment.RestoreData) getIntent().getSerializableExtra(PAGE_ONE_DATA);
                    NewsFragment newsFragment = NewsFragment.newInstance(restoreData);
                    newsFragment.setUpdateUnReadMsgDelegate(MainActivity.this);
                    fragment = newsFragment;
                    break;
                case 1:
                    restoreData = (BaseHomeFragment.RestoreData) getIntent().getSerializableExtra(PAGE_TWO_DATA);
                    MsgFragment msgFragment = MsgFragment.newInstance(restoreData);
                    msgFragment.setUpdateUnReadMsgDelegate(MainActivity.this);
                    fragment = msgFragment;
                    break;
                case 2:
                    restoreData = (BaseHomeFragment.RestoreData) getIntent().getSerializableExtra(PAGE_THREE_DATA);
                    fragment = NodesNavFragment.newInstance(restoreData);
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
