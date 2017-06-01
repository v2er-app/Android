package me.ghui.v2ex.module.user;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerUserHomeComponent;
import me.ghui.v2ex.injector.module.UserHomeModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.network.bean.UserPageInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;
import me.ghui.v2ex.widget.listener.AppBarStateChangeListener;

/**
 * Created by ghui on 04/05/2017.
 * 用户主页
 */

public class UserHomeActivity extends BaseActivity<UserHomeContract.IPresenter> implements
        MultiItemTypeAdapter.OnItemClickListener, UserHomeContract.IView {

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.user_img)
    ImageView mUserImg;
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
    Toolbar mToolbar;
    @BindView(R.id.user_online_tv)
    TextView mOnlineTv;
    @BindView(R.id.user_follow_ct)
    CheckedTextView mUserFollowCt;


    private UserPageInfo mUserPageInfo;

    @Inject
    MultiItemTypeAdapter<UserPageInfo.Item> mAdapter;
    public static final String USER_NAME_KEY = KEY("user_name");
    private String mUserName;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_user_page;
    }

    @Override
    protected Toolbar attachToolbar() {
        return null;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mUserName = intent.getStringExtra(USER_NAME_KEY);
    }

    @Override
    protected void startInject() {
        DaggerUserHomeComponent.builder()
                .appComponent(getAppComponent())
                .userHomeModule(new UserHomeModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    mToolbar.setTitle(null);
                    mToolbar.setSubtitle(null);
                } else if (state == State.COLLAPSED) {
                    mToolbar.setTitle(mUserName);
                    mToolbar.setSubtitle(mUserPageInfo.isOnline() ? "ONLINE" : null);
                } else {
                    //中间状态
                }
            }
        });
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
        Glide.with(this)
                .load(userPageInfo.getAvatar())
                .into(mUserImg);
        Glide.with(this).load(userPageInfo.getAvatar())
                .bitmapTransform(new BlurTransformation(this))
                .into(mBigImgBg);
        mUserText.setText(userPageInfo.getUserName());
        mUserDesTv.setText(userPageInfo.getDesc());
        mOnlineTv.setVisibility(userPageInfo.isOnline() ? View.VISIBLE : View.GONE);
        mUserFollowCt.setChecked(userPageInfo.isFollowed());
        mUserFollowCt.setText(userPageInfo.isFollowed() ? "已关注" : "+ 关注");

        mAdapter.setData(userPageInfo.getItems());
        mRecyclerView.setHasMore(false);
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        String link = mAdapter.getDatas().get(position).getTopicLink();
        Navigator.from(getContext())
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_LINK_KEY, link)
                .start();
    }
}
