package me.ghui.v2er.module.drawer.star;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.CSlidingTabLayout;

/**
 * Created by ghui on 17/05/2017.
 */

public class StarActivity extends BaseActivity {
    private static final String TAB_INDEX = KEY("tab_index");
    @BindView(R.id.star_toolbar)
    BaseToolBar  mToolBar;
    @BindView(R.id.tablayout_main)
    CSlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>(2);

    public static void open(Context context) {
        open(context, 0);
    }

    public static void open(Context context, @IntRange(from = 0, to = 1) int tabIndex) {
        Navigator.from(context)
                .putExtra(TAB_INDEX, tabIndex)
                .to(StarActivity.class).start();
    }


    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_star;
    }

    protected void configToolBar() {
        mToolBar.setTitle(getTitle());
        mToolBar.setElevation(0);
        mToolBar.setOnDoubleTapListener(this);
        mToolBar.setNavigationOnClickListener(view -> {
            if (isTaskRoot()) finishToHome();
            else onBackPressed();
        });
        Utils.setPaddingForStatusBar(mToolBar);
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
        mFragments.add(TopicStarFragment.newInstance());
        mFragments.add(NodeStarFragment.newInstance());
        mSlidingTabLayout.setViewPager(mViewPager, new String[]{"主题", "节点"}, getActivity(), mFragments);
    }
}
