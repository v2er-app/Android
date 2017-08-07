package me.ghui.v2er.module.home;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.widget.MsgView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.OnFragmentReEnter;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.create.CreateTopicActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.star.StarActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.settings.SettingActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.FollowProgressBtn;

public class MainActivity extends BaseActivity implements View.OnClickListener, UpdateUnReadMsgDelegate, CheckInContract.IView {

    private final String[] TAB_TITLES = {"全部", "消息", "节点"};
    private ArrayList<Fragment> mFragments = new ArrayList<>(3);

    @BindView(R.id.left_draw_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationview_main)
    NavigationView mNavigationView;
    @BindView(R.id.tablayout_main)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    @BindView(R.id.main_toolbar)
    BaseToolBar mToolbar;

    private View mNavHeaderView;
    private ImageView mAvatarImg;
    private TextView mUserNameTv;
    private FollowProgressBtn mCheckInBtn;
    private MenuItem mCreateMenuItem;
    private CheckInPresenter mCheckInPresenter;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_main;
    }

    @Override
    protected void configSystemBars(Window window) {
        super.configSystemBars(window);
        Utils.transparentBars(window, Color.TRANSPARENT, getResources().getColor(R.color.transparent_navbar_color));
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    protected void configToolBar() {
        Utils.setPaddingForStatusBar(mToolbar);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setElevation(0);
        mToolbar.setNavigationIcon(R.drawable.main_navigation_icon);
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
//            else {
//                if (PreConditions.notLoginAndProcessToLogin(getContext())) return true;
//                Navigator.from(getContext()).to(CreateTopicActivity.class).start();
//            }
            return true;
        });
    }

    @Override
    public boolean onToolbarDoubleTaped() {
        int index = mSlidingTabLayout.getCurrentTab();
        View rootView = mFragments.get(index).getView();
        if (rootView == null) return false;
        RecyclerView recyclerView = rootView.findViewById(R.id.base_recyclerview);
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        configToolBar();
        mNavHeaderView = mNavigationView.getHeaderView(0);
        mAvatarImg = mNavHeaderView.findViewById(R.id.avatar_img);
        mUserNameTv = mNavHeaderView.findViewById(R.id.user_name_tv);
        mCheckInBtn = mNavHeaderView.findViewById(R.id.check_in_progress_btn);
        mAvatarImg.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        mCheckInBtn.setOnClickListener(this);
        updateHeaderView();

        NewsFragment newsFragment = NewsFragment.newInstance();
        newsFragment.setUpdateUnReadMsgDelegate(this);
        MsgFragment msgFragment = MsgFragment.newInstance();
        msgFragment.setUpdateUnReadMsgDelegate(this);
        mFragments.add(newsFragment);
        mFragments.add(msgFragment);
        mFragments.add(NodesNavFragment.newInstance());

        mCreateMenuItem = mNavigationView.getMenu().findItem(R.id.create_nav_item);
        mCreateMenuItem.setVisible(Pref.readBool(R.string.pref_key_hide_create_btn));
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
                case R.id.feedback_nav_item:
                    Utils.sendOfficalV2erEmail(this);
                    break;
                case R.id.create_nav_item:
                    if (PreConditions.notLoginAndProcessToLogin(getContext())) return true;
                    Navigator.from(getContext()).to(CreateTopicActivity.class).start();
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                updateHeaderView();
                mCreateMenuItem.setVisible(Pref.readBool(R.string.pref_key_hide_create_btn));
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES, getActivity(), mFragments);
        initCheckIn();
    }

    private void initCheckIn() {
        mCheckInPresenter = new CheckInPresenter(this);
        mCheckInPresenter.start();
    }

    private void updateHeaderView() {
        UserInfo userInfo = UserUtils.getUserInfo();
        if (userInfo != null) {
            mUserNameTv.setText(userInfo.getUserName());
            Picasso.with(getContext())
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
                    toast("今日已签到!");
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

        if (Pref.readBool(R.string.pref_key_keep_activity)) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
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
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (mFragments.get(getCurrentTab()) instanceof OnFragmentReEnter) {
            ((OnFragmentReEnter) mFragments.get(getCurrentTab())).onFragmentReEnter();
        }
    }

    @Override
    public FollowProgressBtn checkInBtn() {
        return mCheckInBtn;
    }
}
