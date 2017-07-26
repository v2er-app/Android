package me.ghui.v2er.module.user;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.injector.component.DaggerUserHomeComponent;
import me.ghui.v2er.injector.module.UserHomeModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.UserPageInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;

/**
 * Created by ghui on 04/05/2017.
 * 用户主页
 */

public class UserHomeActivity extends BaseActivity<UserHomeContract.IPresenter> implements
        MultiItemTypeAdapter.OnItemClickListener, UserHomeContract.IView {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
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
    @BindView(R.id.user_follow_ct)
    TextView mUserFollowCt;
    @BindView(R.id.user_block_ct)
    TextView mUserBlockCt;
    private UserPageInfo mUserPageInfo;

    @Inject
    MultiItemTypeAdapter<UserPageInfo.Item> mAdapter;
    public static final String USER_NAME_KEY = KEY("user_name_key");
    private static final String USER_AVATAR_KEY = KEY("user_avatar_key");
    private static final String USER_SHARE_ELEMENT_AVATAR_KEY = KEY("user_share_element_avatar_key");
    private String mUserName;
    private String mAvatar;
    private boolean mIsReturning;
    private boolean isAppbarExpanted;
    private String mTransitionName;


    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning && !isAppbarExpanted) {
                names.clear();
                sharedElements.clear();
            }
        }
    };

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
    protected void configSystemBars(Window window) {
        super.configSystemBars(window);
        Utils.transparentBars(getWindow(), Color.TRANSPARENT, getResources().getColor(R.color.transparent_navbar_color));
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
        return PreConditions.notEmpty(mAvatar);
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        super.finishAfterTransition();
    }

    @Override
    protected void init() {
        mAvatarImg.setTransitionName(mTransitionName);
        Utils.setPaddingForStatusBar(mToolbar);
        setEnterSharedElementCallback(mCallback);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
//        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                    mToolbar.setSubtitle(isOnLine ? "ONLINE" : null);
                } else {
                    //中间状态
                }
            }
        });
        mUserText.setText(mUserName);
        fillAvatar();
    }

    private void fillAvatar() {
        if (PreConditions.isEmpty(mAvatar)) return;
        Picasso.with(this).load(mAvatar)
                .transform(new BlurTransformation(this))
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .into(mBigImgBg, new Callback() {
                    @Override
                    public void onSuccess() {
                        scheduleStartPostponedTransition(mAvatarImg);
                    }

                    @Override
                    public void onError() {
                        onSuccess();
                    }
                });
        Logger.d("NewsAvatar:2 " + mAvatar);
        Picasso.with(this)
                .load(mAvatar)
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .into(mAvatarImg);

    }

    @Override
    protected void autoLoad() {
        mPresenter.start();
    }

    @Override
    public String getUsername() {
        return mUserName;
    }

    @Override
    public void fillView(UserPageInfo userPageInfo) {
        mUserPageInfo = userPageInfo;
        mUserFollowCt.setVisibility(View.VISIBLE);
        mUserBlockCt.setVisibility(View.VISIBLE);
        if (mAvatarImg.getDrawable() == null) {
            Logger.d("NewsAvatar:3 " + userPageInfo.getAvatar());
            Picasso.with(this)
                    .load(userPageInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(mAvatarImg);
            Picasso.with(this).load(userPageInfo.getAvatar())
                    .transform(new BlurTransformation(this))
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
        mUserBlockCt.setText(hadBlocked ? "已屏蔽" : "屏蔽");
    }

    private void toggleFollowBtnStatus(boolean hadFollowed) {
        mUserFollowCt.setText(hadFollowed ? "已关注" : "关注");
    }


    @OnClick(R.id.user_block_ct)
    void onBlockClicked() {
        if (!mUserPageInfo.hadBlocked()) {
            new ConfirmDialog.Builder(this)
                    .title("屏蔽用户")
                    .msg("确定屏蔽用户@" + mUserName + "吗？")
                    .positiveText(R.string.ok, dialog -> mPresenter.blockUser(mUserPageInfo.getBlockUrl()))
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            mPresenter.blockUser(mUserPageInfo.getBlockUrl());
        }
    }

    @OnClick(R.id.user_follow_ct)
    void onFollowClicked(TextView checkedTextView) {
        if (mUserPageInfo.hadFollowed()) {
            new ConfirmDialog.Builder(this)
                    .title("取消关注")
                    .msg("确定取消关注@" + mUserName + "吗？")
                    .positiveText(R.string.ok, dialog -> mPresenter.followUser(mUserPageInfo.getUserName(), mUserPageInfo.getFollowUrl()))
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
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
        TopicActivity.open(mAdapter.getDatas().get(position).getTopicLink(), this);
    }
}
