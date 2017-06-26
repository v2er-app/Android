package me.ghui.v2er.module.home;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.widget.MsgView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.star.StarActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.settings.SettingActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

public class MainActivity extends BaseActivity implements View.OnClickListener, UpdateUnReadMsgDelegate {

    private final String[] TAB_TITLES = {"最新", "消息", "节点"};
    private ArrayList<Fragment> mFragments = new ArrayList<>(3);

    @BindView(R.id.left_draw_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationview_main)
    NavigationView mNavigationView;
    @BindView(R.id.tablayout_main)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;

    private View mNavHeaderView;
    private ImageView mAvatarImg;
    private TextView mUserNameTv;

    @Override
    protected int attachToolBar() {
        return R.layout.toolbar_main_act;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_main;
    }

    @Override
    protected void configSystemBars(Window window) {
        Utils.transparentBars(window, Color.TRANSPARENT, getColor(R.color.transparent_navbar_color));
        window.setBackgroundDrawableResource(R.drawable.default_window_drawable);
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        Utils.setPaddingForStatusBar(toolBar);
        toolBar.setElevation(0);
        toolBar.setNavigationIcon(R.drawable.main_navigation_icon);
        toolBar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
        toolBar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
        toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                pushFragment(SearchFragment.newInstance());
            }
            return true;
        });
    }

    @Override
    protected void init() {
        mNavHeaderView = mNavigationView.getHeaderView(0);
        mAvatarImg = (ImageView) mNavHeaderView.findViewById(R.id.avatar_img);
        mUserNameTv = (TextView) mNavHeaderView.findViewById(R.id.user_name_tv);
        mAvatarImg.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        updateHeaderView();

        NewsFragment newsFragment = NewsFragment.newInstance();
        newsFragment.setUpdateUnReadMsgDelegate(this);
        MsgFragment msgFragment = MsgFragment.newInstance();
        msgFragment.setUpdateUnReadMsgDelegate(this);
        mFragments.add(newsFragment);
        mFragments.add(msgFragment);
        mFragments.add(NodesNavFragment.newInstance());

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
//                    Instabug.invoke(InstabugInvocationMode.NEW_FEEDBACK);
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                updateHeaderView();
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES, getActivity(), mFragments);
    }

    private void updateHeaderView() {
        UserInfo userInfo = UserUtils.getUserInfo();
        if (userInfo != null) {
            mUserNameTv.setText(userInfo.getUserName());
            Glide.with(getContext())
                    .load(userInfo.getAvatar())
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
                break;
        }
        mDrawerLayout.closeDrawers();
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

        if (getCurrentTab() != 0) {
            mSlidingTabLayout.setCurrentTab(0);
            return;
        }
        if (isBackableEmpty() && Pref.readBool(R.string.pref_key_keep_activity)) {
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


}
