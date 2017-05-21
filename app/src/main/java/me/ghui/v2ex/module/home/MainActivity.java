package me.ghui.v2ex.module.home;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2ex.R;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.drawer.care.SpecialCareActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2ex.module.drawer.setting.SettingActivity;
import me.ghui.v2ex.module.drawer.star.StarActivity;
import me.ghui.v2ex.module.login.LoginActivity;
import me.ghui.v2ex.module.user.UserInfoActivity;
import me.ghui.v2ex.network.bean.UserInfo;
import me.ghui.v2ex.util.UserUtils;
import me.ghui.v2ex.widget.BaseToolBar;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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
    protected void configToolBar(BaseToolBar toolBar) {
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
                Toast.makeText(getContext(), "do Search...", Toast.LENGTH_SHORT).show();
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

        mFragments.add(NewsFragment.newInstance());
        mFragments.add(MsgFragment.newInstance());
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
                    Navigator.from(this).to(UserInfoActivity.class).start();
                } else {
                    Navigator.from(this).to(LoginActivity.class).start();
                }
                break;
        }
        mDrawerLayout.closeDrawers();
    }


}
