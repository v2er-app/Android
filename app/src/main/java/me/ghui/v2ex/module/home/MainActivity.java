package me.ghui.v2ex.module.home;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2ex.R;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2ex.widget.BaseToolBar;

public class MainActivity extends BaseActivity {

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
		mFragments.add(NewsFragment.newInstance());
		mFragments.add(MsgFragment.newInstance());
		mFragments.add(NodesFragment.newInstance());

		mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.hot_nav_item:
                    Navigator.from(getContext()).to(DailyHotActivity.class).start();
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });
		mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES, getActivity(), mFragments);
	}


}
