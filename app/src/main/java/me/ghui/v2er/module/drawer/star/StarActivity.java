package me.ghui.v2er.module.drawer.star;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ColorModeReloader;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.CSlidingTabLayout;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;

/**
 * Created by ghui on 17/05/2017.
 */

public class StarActivity extends BaseActivity {
    private static final String TAB_INDEX = KEY("tab_index");
    private static final String PAGE_ONE_DATA = KEY("page_one_data");
    private static final String PAGE_TWO_DATA = KEY("page_two_data");
    private static final String TOPIC_IS_APPBAR_EXPANDED = KEY("topic_is_appbar_expanded");

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.star_toolbar)
    BaseToolBar mToolbar;
    @BindView(R.id.tablayout_main)
    CSlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>(2);
    TopicStarFragment mTopicStarFragment;
    NodeStarFragment mNodeStarFragment;
    private boolean isAppbarExpanted = true;

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
        mToolbar.setTitle(getTitle());
        mToolbar.setElevation(0);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setNavigationOnClickListener(view -> {
            if (isTaskRoot()) finishToHome();
            else onBackPressed();
        });
        Utils.setPaddingForStatusBar(mToolbar);
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
        TopicStarFragment.RestoreData restoreData1 = (TopicStarFragment.RestoreData) getIntent()
                .getSerializableExtra(PAGE_ONE_DATA);
        NodeStarFragment.RestoreData restoreData2 = (NodeStarFragment.RestoreData) getIntent()
                .getSerializableExtra(PAGE_TWO_DATA);
        mFragments.add(mTopicStarFragment = TopicStarFragment.newInstance(restoreData1));
        mFragments.add(mNodeStarFragment = NodeStarFragment.newInstance(restoreData2));
        mSlidingTabLayout.setViewPager(mViewPager, new String[]{"主题", "节点"}, getActivity(), mFragments);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mSlidrInterface == null) return;
                if (position == 0) {
                    mSlidrInterface.unlock();
                } else {
                    mSlidrInterface.lock();
                }
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                isAppbarExpanted = state == State.EXPANDED;
            }
        });
        int padding = ScaleUtils.dp(6f);
        mSlidingTabLayout.setTitleViewVerticalPadding(0, padding);
        mSlidingTabLayout.setTitleViewVerticalPadding(1, padding);
        int index = getIntent().getIntExtra(TAB_INDEX, 0);
        mSlidingTabLayout.setCurrentTab(index);
        isAppbarExpanted = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, true);
        mAppBarLayout.setExpanded(isAppbarExpanted);
    }

    @Override
    protected void autoLoad() {
        super.autoLoad();
    }

    @Override
    protected void reloadMode(int mode) {
        ColorModeReloader.target(this)
                .putExtra(TAB_INDEX, mSlidingTabLayout.getCurrentTab())
                .putExtra(PAGE_ONE_DATA, mTopicStarFragment.getRestoreData())
                .putExtra(PAGE_TWO_DATA, mNodeStarFragment.getRestoreData())
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanted)
                .reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.configToolbarScroll(mToolbar);
    }
}
