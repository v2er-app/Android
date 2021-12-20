package me.ghui.v2er.module.home;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.helper.BottomNavigationViewHelper;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;

public class MainActivity extends BaseActivity implements View.OnClickListener, UpdateUnReadMsgDelegate,
        HomeFilterMenu.OnMenuItemClickListener {

    private static final String PAGE_ONE_DATA = KEY("page_one_data");
    private static final String PAGE_TWO_DATA = KEY("page_two_data");
    private static final String PAGE_THREE_DATA = KEY("page_three_data");
    private static final String PAGE_FOUR_DATA = KEY("page_four_data");
    private static final String TOPIC_IS_APPBAR_EXPANDED = KEY("toolbar_is_appbar_expanded");

    public static boolean isAlive;
    private final int[] titles = {R.string.feed, R.string.explore,
            R.string.message, R.string.mine};
    private final int[] bottomNavigationViewItemIds = {R.id.feed_page, R.id.explore_page,
            R.id.message_page, R.id.mine_page};
    @BindView(R.id.main_logo)
    ImageView mLogoView;
    @BindView(R.id.main_bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    @BindView(R.id.main_toolbar)
    BaseToolBar mToolbar;
    @BindView(R.id.main_container)
    ViewGroup mMainContainer;
    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;

    private NewsFragment mNewsFragment;
    private MsgFragment mMsgFragment;
    private NodesNavFragment mNavFragment;
    private MineFragment mMineFragment;
    private boolean isAppbarExpanded = true;


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
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setElevation(0);
        mToolbar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
        mToolbar.setTitle("");
        mLogoView.setVisibility(View.VISIBLE);
        mToolbar.setViewTileCenter(mLogoView);
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

    private void changeTitle(int position) {
        switch (position) {
            case 0:
                mToolbar.setTitle("");
                mLogoView.setVisibility(View.VISIBLE);
                mToolbar.setViewTileCenter(mLogoView);
                break;
            case 1:
            case 2:
            case 3:
                mLogoView.setVisibility(View.GONE);
                mToolbar.setTitle(titles[position]);
                mToolbar.setTileCenter(true);
                break;
        }
    }

    @Override
    protected void init() {
        isAlive = true;
        configToolBar();
        BottomNavigationViewHelper.setImageSize(mBottomNavigationView,
                getResources().getDimensionPixelSize(R.dimen.bottom_navigation_view_icon_small_size),
                getResources().getDimensionPixelSize(R.dimen.bottom_navigation_view_icon_small_size));
        mViewPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomNavigationView.setSelectedItemId(bottomNavigationViewItemIds[position]);
                changeTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.feed_page:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.explore_page:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.message_page:
                    mViewPager.setCurrentItem(2);
                    break;
                case R.id.mine_page:
                    mViewPager.setCurrentItem(3);
                    break;
                default:
                    return false;
            }
            return true;
        });
        isAppbarExpanded = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, true);
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanded)
                .putExtra(PAGE_ONE_DATA, mNewsFragment.getRestoreData())
                .putExtra(PAGE_TWO_DATA, mNavFragment.getRestoreData())
                .putExtra(PAGE_THREE_DATA, mMsgFragment.getRestoreData())
                .reload();
    }

    @Override
    public void onClick(View v) { }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        isBackableEmpty();
        super.onBackPressed();
    }

    @Override
    public void updateUnReadMsg(int position, int count) {
//        if (count <= 0) {//hide
//            mSlidingTabLayout.hideMsg(position);
//        } else {
//            mSlidingTabLayout.showMsg(position, count);
//            //config sliding msgview
//            float padding = getResources().getDimension(R.dimen.mediumTextSize) / 2f;
//            mSlidingTabLayout.setMsgMargin(1, padding * 0.92f, padding * 0.28f);
//            MsgView msgView = mSlidingTabLayout.getMsgView(1);
//            float textSize = getResources().getDimension(R.dimen.tinyTextSize);
//            msgView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
//            lp.width = Math.round(textSize * (count + "").length() * 1.5f);
//            lp.height = Math.round(textSize * 1.5f);
//            msgView.setLayoutParams(lp);
//        }
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
        int pos = mViewPager.getCurrentItem();
        switch (pos) {
            case 0:
                return mNewsFragment;
            case 1:
                return mNavFragment;
            case 2:
                return mMsgFragment;
            case 3:
                return mMineFragment;
        }
        return null;
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
                    fragment = NodesNavFragment.newInstance(restoreData);
                    break;
                case 2:
                    restoreData = (BaseHomeFragment.RestoreData) getIntent().getSerializableExtra(PAGE_THREE_DATA);
                    MsgFragment msgFragment = MsgFragment.newInstance(restoreData);
                    msgFragment.setUpdateUnReadMsgDelegate(MainActivity.this);
                    fragment = msgFragment;
                    break;
                case 3:
                    restoreData = (BaseHomeFragment.RestoreData) getIntent().getSerializableExtra(PAGE_FOUR_DATA);
                    fragment = MineFragment.newInstance(restoreData);
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
                    mNavFragment = (NodesNavFragment) fragment;
                    break;
                case 2:
                    mMsgFragment = (MsgFragment) fragment;
                    break;
                case 3:
                    mMineFragment = (MineFragment) fragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

    };
}
