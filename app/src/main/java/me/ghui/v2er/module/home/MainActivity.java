package me.ghui.v2er.module.home;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.widget.MsgView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.App;
import me.ghui.v2er.helper.BottomNavigationViewHelper;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.GeneralError;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Theme;
import me.ghui.v2er.util.UnreadMsgUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.FollowProgressBtn;

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
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.viewpager_main)
    ViewPager mViewPager;
    @BindView(R.id.main_toolbar)
    BaseToolBar mToolbar;
    @BindView(R.id.main_container)
    ViewGroup mMainContainer;
    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;
    DrawerLayout mainDrawerLayout;
    NavigationRailView mainNavigationRailView;

    private NewsFragment mNewsFragment;
    private MsgFragment mMsgFragment;
    private ExploreFragment mExploreFragment;
    private MineFragment mMineFragment;
    private CheckInPresenter mCheckInPresenter;
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

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public int attachOptionsMenuRes() {
        return R.menu.main_toolbar_menu;
    }

    @Override
    public void configOptionsMenu(Menu menu) {
        super.configOptionsMenu(menu);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setTitle("");
        mLogoView.setVisibility(View.VISIBLE);
        mToolbar.setViewTileCenter(mLogoView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            pushFragment(SearchFragment.newInstance());
        }
        return true;
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
                mToolbar.setTitleTextColor(Theme.getColor(R.attr.icon_tint_color, getContext()));
                break;
        }
    }

    private void initCheckIn() {
        if (UserUtils.isLogin()) {
            mCheckInPresenter = new CheckInPresenter(new CheckInContract.ICheckInCallBack() {

                @Override
                public void onHasChekIn(String checkInDays) {

                }

                @Override
                public void onCheckInSuccess(String checkInDays) {
                    toast("签到成功/" + checkInDays + "天");
                }

                @Override
                public void onCheckInFail() {
                    toast("签到遇到问题!");
                }

            });
            mCheckInPresenter.start();
        }
    }

    private void initBottomNavigationView() {
        mBottomNavigationView = findViewById(R.id.main_bottom_navigation_view);
        BottomNavigationViewHelper.setImageSize(mBottomNavigationView,
                getResources().getDimensionPixelSize(R.dimen.bottom_navigation_view_icon_small_size),
                getResources().getDimensionPixelSize(R.dimen.bottom_navigation_view_icon_small_size));
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
    }

    private void initLeftNavigationView() {
        mainNavigationRailView = findViewById(R.id.mainNavigationRailView);
        mainNavigationRailView.setOnItemSelectedListener(item -> {
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
    }

    private void initNavigationView() {
        int screenOrientation = getResources().getConfiguration().orientation;
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                initLeftNavigationView();
                break;
            }
            case Configuration.ORIENTATION_PORTRAIT: {
                initBottomNavigationView();
                break;
            }
            case Configuration.ORIENTATION_SQUARE:
            case Configuration.ORIENTATION_UNDEFINED:
                break;
        }
    }

    private void showUnReadMsg() {
        int screenOrientation = getResources().getConfiguration().orientation;
        if (App.get().unReadMsgCount > 0) {
            if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                BottomNavigationViewHelper.showBadgeView(this, mBottomNavigationView,
                        2, App.get().unReadMsgCount);
            } else {
                BottomNavigationViewHelper.showBadgeView(this, mainNavigationRailView,
                        2, App.get().unReadMsgCount);
            }
        }
    }

    @Override
    protected void init() {
        isAlive = true;
        mViewPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int screenOrientation = getResources().getConfiguration().orientation;
                if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    mBottomNavigationView.setSelectedItemId(bottomNavigationViewItemIds[position]);
                } else {
                    mainNavigationRailView.setSelectedItemId(bottomNavigationViewItemIds[position]);
                }
                changeTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initNavigationView();
        showUnReadMsg();
        isAppbarExpanded = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, true);
        initCheckIn();
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanded)
                .putExtra(PAGE_ONE_DATA, mNewsFragment.getRestoreData())
                .putExtra(PAGE_TWO_DATA, mExploreFragment.getRestoreData())
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
        App.get().unReadMsgCount = count;
        showUnReadMsg();
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
                return mExploreFragment;
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
                    fragment = ExploreFragment.newInstance(restoreData);
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
                    mExploreFragment = (ExploreFragment) fragment;
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
