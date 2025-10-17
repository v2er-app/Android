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
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.util.VshareVersionChecker;
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
    private boolean isAppbarExpanded = true;
    private View mVshareBadge;
    private VshareVersionChecker mVshareVersionChecker;
    private android.graphics.drawable.Drawable mOriginalNavIcon;
    private android.graphics.drawable.LayerDrawable mNavIconWithBadge;

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
        // Apply padding to AppBarLayout for status bar
        Utils.setPaddingForStatusBar(mAppBarLayout);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setElevation(0);
        mToolbar.setNavigationIcon(R.drawable.nav);
        mToolbar.setNavigationContentDescription(R.string.acc_nav_menu);
        mToolbar.getNavigationIcon().setTint(Theme.getColor(R.attr.icon_tint_color, this));
        mToolbar.inflateMenu(R.menu.main_toolbar_menu);//设置右上角的填充菜单
        mToolbar.setNavigationOnClickListener(v -> {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        // Initialize toolbar badge support for hamburger icon
        setupNavigationIconBadge();
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                pushFragment(SearchFragment.newInstance());
            }
            return true;
        });
    }

    private void setupNavigationIconBadge() {
        // Save the original navigation icon
        mOriginalNavIcon = getDrawable(R.drawable.nav).mutate();
        mOriginalNavIcon.setTint(Theme.getColor(R.attr.icon_tint_color, this));

        // Create a red dot drawable for the badge
        android.graphics.drawable.ShapeDrawable badge = new android.graphics.drawable.ShapeDrawable(
                new android.graphics.drawable.shapes.OvalShape());
        badge.getPaint().setColor(0xFFF44336); // Red color
        badge.setIntrinsicHeight(ScaleUtils.dp(8));
        badge.setIntrinsicWidth(ScaleUtils.dp(8));

        // Create a LayerDrawable with the navigation icon and badge
        android.graphics.drawable.Drawable[] layers = new android.graphics.drawable.Drawable[2];
        layers[0] = mOriginalNavIcon;
        layers[1] = badge;

        mNavIconWithBadge = new android.graphics.drawable.LayerDrawable(layers);

        // Position the badge at the top-right corner of the icon
        // Navigation icon is 24dp, badge is 8dp
        mNavIconWithBadge.setLayerSize(1, ScaleUtils.dp(8), ScaleUtils.dp(8));
        mNavIconWithBadge.setLayerGravity(1, Gravity.TOP | Gravity.END);
        mNavIconWithBadge.setLayerInsetEnd(1, -ScaleUtils.dp(4)); // Overlap with icon edge
        mNavIconWithBadge.setLayerInsetTop(1, -ScaleUtils.dp(4));  // Overlap with icon edge
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

        Utils.setPaddingForStatusBar(mNavigationView);
        mNavigationView.setItemIconTintList(null);
        mNavHeaderView = mNavigationView.getHeaderView(0);
        mAvatarImg = mNavHeaderView.findViewById(R.id.avatar_img);
        mUserNameTv = mNavHeaderView.findViewById(R.id.user_name_tv);
        mCheckInBtn = mNavHeaderView.findViewById(R.id.check_in_progress_btn);
        mAvatarImg.setOnClickListener(this);
        mUserNameTv.setOnClickListener(this);
        mCheckInBtn.setOnClickListener(this);

        // Apply font size to navigation header
        mUserNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());

        // Apply font size scaling to navigation menu items
        applyFontSizeToNavigationMenu();
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
                case R.id.vshare_nav_item:
                    me.ghui.v2er.module.vshare.VshareWebActivity.open(getContext());
                    mVshareVersionChecker.markAsViewed();
                    updateVshareBadge();
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
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                super.onOffsetChanged(appBarLayout, verticalOffset);
                // Calculate toolbar's visibility based on its position
                int toolbarHeight = mToolbar.getHeight();
                int statusBarHeight = Utils.getStatusBarHeight();

                // When toolbar is scrolled up and would be under status bar, hide it
                if (verticalOffset < 0 && Math.abs(verticalOffset) >= toolbarHeight - statusBarHeight) {
                    mToolbar.setVisibility(View.INVISIBLE);
                } else {
                    mToolbar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                // Only update when fully expanded or collapsed, ignore IDLE state
                if (state == State.EXPANDED) {
                    isAppbarExpanded = true;
                    mToolbar.setVisibility(View.VISIBLE);
                } else if (state == State.COLLAPSED) {
                    isAppbarExpanded = false;
                    mToolbar.setVisibility(View.INVISIBLE);
                }
                // IDLE state doesn't change the expanded status
            }
        });

        TAB_TITLES[0] = TabInfo.getSelectTab().title;
        mViewPager.setAdapter(new SlidePagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setViewPager(mViewPager, TAB_TITLES);
        mSlidingTabLayout.setOnTabSelectListener(this);
        configNewsTabTitle();
        initCheckIn();
        initVshareVersionChecker();

        int index = getIntent().getIntExtra(TAB_INDEX, 0);
        mSlidingTabLayout.setCurrentTab(index);
        isAppbarExpanded = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, true);
        mAppBarLayout.setExpanded(isAppbarExpanded);
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(TAB_INDEX, mSlidingTabLayout.getCurrentTab())
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanded)
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

    private void initVshareVersionChecker() {
        // Initialize the version checker
        mVshareVersionChecker = new VshareVersionChecker(getContext());

        // Delay to ensure menu is fully initialized
        mNavigationView.post(() -> {
            // Find vshare menu item and get the badge view
            MenuItem vshareItem = mNavigationView.getMenu().findItem(R.id.vshare_nav_item);
            if (vshareItem != null && vshareItem.getActionView() != null) {
                mVshareBadge = vshareItem.getActionView().findViewById(R.id.vshare_badge_dot);
                L.d("Vshare badge view found: " + (mVshareBadge != null));
            } else {
                L.e("Vshare menu item or action view is null");
            }

            // Check for version updates
            mVshareVersionChecker.checkForUpdate()
                    .subscribe(hasUpdate -> {
                        L.d("Vshare version check result: hasUpdate=" + hasUpdate);
                        updateVshareBadge(hasUpdate);
                    }, throwable -> {
                        // Log errors for debugging
                        L.e("VshareVersionChecker error: " + throwable.getMessage());
                        throwable.printStackTrace();
                    });
        });
    }

    private void updateVshareBadge() {
        updateVshareBadge(false);
    }

    private void updateVshareBadge(boolean show) {
        L.d("Setting vshare badge visibility: " + (show ? "VISIBLE" : "GONE"));

        // Update menu badge
        if (mVshareBadge != null) {
            mVshareBadge.setVisibility(show ? View.VISIBLE : View.GONE);
        } else {
            L.e("Cannot update menu badge: mVshareBadge is null");
        }

        // Update toolbar navigation icon with badge
        if (mNavIconWithBadge != null && mOriginalNavIcon != null) {
            mToolbar.setNavigationIcon(show ? mNavIconWithBadge : mOriginalNavIcon);
        }
    }

    private void applyFontSizeToNavigationMenu() {
        // Apply font size based on preference
        // This is better handled by setting text appearance in styles
        // We'll use a post delay to ensure the menu is fully initialized
        mNavigationView.postDelayed(() -> {
            applyFontScalingToMenuItems();
        }, 100);
    }

    private void applyFontScalingToMenuItems() {
        // Apply scaling only once to avoid repeated scaling
        View menuView = mNavigationView.getChildAt(0);
        if (menuView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) menuView;
            // Apply to currently visible items
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);
                if (child != null && child.getTag(R.id.font_scaled_tag) == null) {
                    applyScalingToView(child);
                    child.setTag(R.id.font_scaled_tag, true);
                }
            }

            // Set up listener for future items but check if already scaled
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    if (view.getTag(R.id.font_scaled_tag) == null) {
                        applyScalingToView(view);
                        view.setTag(R.id.font_scaled_tag, true);
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {
                    // No action needed
                }
            });
        }
    }

    private void applyScalingToView(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Save original size if not already saved
            Object originalSize = textView.getTag(R.id.original_text_size_tag);
            if (originalSize == null) {
                textView.setTag(R.id.original_text_size_tag, textView.getTextSize());
                float baseSize = textView.getTextSize();
                float scaledSize = FontSizeUtil.getScaledSize(baseSize);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledSize);
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyScalingToView(viewGroup.getChildAt(i));
            }
        }
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
