package me.ghui.v2er.module.user;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.DaggerUserHomeComponent;
import me.ghui.v2er.injector.module.UserHomeModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.UserPageInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.FollowProgressBtn;
import me.ghui.v2er.widget.HackRecyclerView;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;

import static me.ghui.v2er.widget.FollowProgressBtn.FINISHED;
import static me.ghui.v2er.widget.FollowProgressBtn.NORMAL;

/**
 * Created by ghui on 04/05/2017.
 * 用户主页
 */

public class UserHomeActivity extends BaseActivity<UserHomeContract.IPresenter> implements
        MultiItemTypeAdapter.OnItemClickListener, UserHomeContract.IView, HackRecyclerView.AppBarTracking {

    public static final String USER_NAME_KEY = KEY("user_name_key");
    private static final String USER_AVATAR_KEY = KEY("user_avatar_key");
    private static final String USER_SHARE_ELEMENT_AVATAR_KEY = KEY("user_share_element_avatar_key");
    private static final String PAGE_DATA_KEY = KEY("page_data");
    private static final String TOPIC_PAGE_Y_POS_KEY = KEY("TOPIC_PAGE_Y_POS_KEY");
    private static final String TOPIC_Y_POS_OFFSET_KEY = KEY("topic_y_pos_offset");
    private static final String TOPIC_IS_APPBAR_EXPANDED = KEY("topic_is_appbar_expanded");
    @BindView(R.id.base_recyclerview)
    HackRecyclerView mRecyclerView;
    @BindView(R.id.user_img)
    ImageView mAvatarImg;
    @BindView(R.id.big_img_bg)
    ImageView mBigImgBg;
    @BindView(R.id.user_name_tv)
    TextView mUserText;
    @BindView(R.id.user_describtion_tv)
    TextView mUserDesTv;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.user_info_appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_info_toobar)
    BaseToolBar mToolbar;
    @BindView(R.id.user_online_tv)
    TextView mOnlineTv;
    @BindView(R.id.user_follow_btn)
    FollowProgressBtn mUserFollowbtn;
    @BindView(R.id.user_block_btn)
    FollowProgressBtn mUserBlockBtn;
    @Inject
    MultiItemTypeAdapter<UserPageInfo.Item> mAdapter;
    private UserPageInfo mUserPageInfo;
    private String mUserName;
    private String mAvatar;
    private boolean mIsReturning;
    private boolean isAppbarExpanted;
    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning && !isAppbarExpanted) {
                names.clear();
                sharedElements.clear();
            }
        }
    };
    private String mTransitionName;
    //for bugfix start: https://stackoverflow.com/questions/45192654/how-to-avoid-collapsingtoolbarlayout-not-being-snapped-or-being-wobbly-when-sc
    private int mAppBarOffset;
    private boolean mAppBarIdle = false;
    //for bugfix end
    private int mAppBarMaxOffset;
    private LinearLayoutManager mLayoutManager;

    public static void open(String userName, Context context, View sourceView, String avatar) {
        if (sourceView != null && sourceView instanceof ImageView) {
            ImageView imgview = (ImageView) sourceView;
            if (ViewUtils.isSameImgRes(imgview, R.drawable.avatar_placeholder_drawable) || imgview.getDrawable() == null) {
                sourceView = null;
            }
        }
        Navigator.from(context)
                .to(UserHomeActivity.class)
                .putExtra(UserHomeActivity.USER_NAME_KEY, userName)
                .putExtra(UserHomeActivity.USER_AVATAR_KEY, avatar)
                .putExtra(UserHomeActivity.USER_SHARE_ELEMENT_AVATAR_KEY, sourceView == null ? null : sourceView.getTransitionName())
                .shareElement(sourceView)
                .start();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_user_page;
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mUserName = intent.getStringExtra(USER_NAME_KEY);
        mAvatar = intent.getStringExtra(USER_AVATAR_KEY);
        mTransitionName = intent.getStringExtra(USER_SHARE_ELEMENT_AVATAR_KEY);
    }

    @Override
    protected void startInject() {
        DaggerUserHomeComponent.builder()
                .appComponent(getAppComponent())
                .userHomeModule(new UserHomeModule(this))
                .build().inject(this);
    }


    @Override
    protected boolean supportShareElement() {
        return Check.notEmpty(mAvatar);
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        super.finishAfterTransition();
    }

    @Override
    protected void init() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mAvatarImg.setTransitionName(mTransitionName);
        Utils.setPaddingForStatusBar(mToolbar);
        setEnterSharedElementCallback(mCallback);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
//        mRecyclerView.addDivider();
        mRecyclerView.setAppBarTracking(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    isAppbarExpanted = true;
                    mToolbar.setTitle(null);
                    mToolbar.setSubtitle(null);
                } else if (state == State.COLLAPSED) {
                    isAppbarExpanted = false;
                    mToolbar.setTitle(mUserName);
                    boolean isOnLine = false;
                    if (mUserPageInfo != null) {
                        isOnLine = mUserPageInfo.isOnline();
                    }
                    mToolbar.setSubtitle(isOnLine ? "[Online]" : null);
                } else {
                    //中间状态
                }
            }

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                super.onOffsetChanged(appBarLayout, verticalOffset);
                mAppBarOffset = verticalOffset;
                mAppBarIdle = (mAppBarOffset >= 0) || (mAppBarOffset <= mAppBarMaxOffset);
            }

        });
        mAppBarLayout.post(() -> mAppBarMaxOffset = -mAppBarLayout.getTotalScrollRange());
        mUserText.setText(mUserName);
        fillAvatar();

        mUserPageInfo = (UserPageInfo) getIntent().getSerializableExtra(PAGE_DATA_KEY);
        if (mUserPageInfo != null) {
            // restore from reloadMode
            isAppbarExpanted = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, false);
            mAppBarLayout.setExpanded(isAppbarExpanted);
            mUserName = mUserPageInfo.getUserName();
            fillView(mUserPageInfo);
            int pos = getIntent().getIntExtra(TOPIC_PAGE_Y_POS_KEY, 0);
            int offset = getIntent().getIntExtra(TOPIC_Y_POS_OFFSET_KEY, 0);
            L.d("1findFirstCompletelyVisibleItemPosition: " + pos + ", offset: " + offset);
            if (!isAppbarExpanted) {
                mToolbar.setTitle(mUserName);
            }
            post(()-> mLayoutManager.scrollToPositionWithOffset(pos, offset));
        }
    }

    @Override
    protected void reloadMode(int mode) {
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = mRecyclerView.getChildAt(0).getTop();
        ActivityReloader.target(this)
                .putExtra(PAGE_DATA_KEY, mUserPageInfo)
                .putExtra(TOPIC_PAGE_Y_POS_KEY, pos)
                .putExtra(TOPIC_Y_POS_OFFSET_KEY, offset)
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanted)
                .reload();
    }

    private void fillAvatar() {
        if (Check.isEmpty(mAvatar)) return;
        GlideApp.with(this).load(mAvatar)
                .transform(new BlurTransformation())
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        scheduleStartPostponedTransition(mAvatarImg);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        scheduleStartPostponedTransition(mAvatarImg);
                        return false;
                    }
                }).into(mBigImgBg);
        L.d("NewsAvatar:2 " + mAvatar);
        GlideApp.with(this)
                .load(mAvatar)
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .into(mAvatarImg);

    }

    @Override
    protected void autoLoad() {
        if (mUserPageInfo == null) {
            mPresenter.start();
        }
    }

    @Override
    public String getUsername() {
        return mUserName;
    }

    @Override
    public void fillView(UserPageInfo userPageInfo) {
        mUserPageInfo = userPageInfo;
        if (!UserUtils.getUserName().equals(mUserName)) {
            mUserFollowbtn.setVisibility(View.VISIBLE);
            mUserBlockBtn.setVisibility(View.VISIBLE);
        }
        if (mAvatarImg.getDrawable() == null) {
            L.d("NewsAvatar:3 " + userPageInfo.getAvatar());
            GlideApp.with(this)
                    .load(userPageInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(mAvatarImg);
            GlideApp.with(this).load(userPageInfo.getAvatar())
                    .transform(new BlurTransformation())
                    .into(mBigImgBg);
        }

        mUserText.setText(userPageInfo.getUserName());
        mUserDesTv.setText(userPageInfo.getDesc());
        mOnlineTv.setVisibility(userPageInfo.isOnline() ? View.VISIBLE : View.GONE);
        toggleBlockBtnStatus(userPageInfo.hadBlocked());
        toggleFollowBtnStatus(userPageInfo.hadFollowed());
        mAdapter.setData(userPageInfo.getItems());
        mRecyclerView.setHasMore(false);
    }

    private void toggleBlockBtnStatus(boolean hadBlocked) {
        if (hadBlocked) {
            mUserBlockBtn.setStatus(FINISHED, "已屏蔽", R.drawable.progress_button_done_icon);
        } else {
            mUserBlockBtn.setStatus(NORMAL, "屏蔽", R.drawable.progress_button_block_icon);
        }
    }

    private void toggleFollowBtnStatus(boolean hadFollowed) {
        if (hadFollowed) {
            mUserFollowbtn.setStatus(FINISHED, "已关注", R.drawable.progress_button_done_icon);
        } else {
            mUserFollowbtn.setStatus(NORMAL, "关注", R.drawable.progress_button_follow_normal_icon);
        }
    }


    @OnClick(R.id.user_block_btn)
    void onBlockClicked(FollowProgressBtn progressBtn) {
        if (!mUserPageInfo.hadBlocked()) {
            new ConfirmDialog.Builder(this)
                    .title("屏蔽用户")
                    .msg("确定屏蔽用户@" + mUserName + "吗？")
                    .positiveText(R.string.ok, dialog -> {
                        progressBtn.startUpdate();
                        mPresenter.blockUser(mUserPageInfo.getBlockUrl());
                    })
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            progressBtn.startUpdate();
            mPresenter.blockUser(mUserPageInfo.getBlockUrl());
        }
    }

    @OnClick(R.id.user_follow_btn)
    void onFollowClicked(FollowProgressBtn progressBtn) {
        if (mUserPageInfo.hadFollowed()) {
            new ConfirmDialog.Builder(this)
                    .title("取消关注")
                    .msg("确定取消关注@" + mUserName + "吗？")
                    .positiveText(R.string.ok, dialog -> {
                        progressBtn.startUpdate();
                        mPresenter.followUser(mUserPageInfo.getUserName(), mUserPageInfo.getFollowUrl());
                    })
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            progressBtn.startUpdate();
            mPresenter.followUser(mUserPageInfo.getUserName(), mUserPageInfo.getFollowUrl());
        }
    }

    @Override
    public void afterBlockUser(boolean hadBlocked) {
        toast(hadBlocked ? "屏蔽成功" : "取消屏蔽成功");
        toggleBlockBtnStatus(hadBlocked);
        mUserPageInfo.updateBlockUrl(!hadBlocked);
    }

    @Override
    public void afterfollowUser(UserPageInfo userPageInfo) {
        mUserPageInfo = userPageInfo;
        toggleFollowBtnStatus(userPageInfo.hadFollowed());
        toast(userPageInfo.hadFollowed() ? "关注成功" : "取消关注成功");
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        UserPageInfo.Item item = mAdapter.getDatas().get(position);
        TopicActivity.open(item.getTopicLink(), this);
    }

    @Override
    public boolean isAppBarExpanded() {
        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }
}
