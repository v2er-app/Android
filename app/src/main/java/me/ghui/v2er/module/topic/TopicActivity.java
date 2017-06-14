package me.ghui.v2er.module.topic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.DaggerTopicComponent;
import me.ghui.v2er.injector.module.TopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicActivity extends BaseActivity<TopicContract.IPresenter> implements TopicContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener {
    private static final String TOPIC_ID_KEY = KEY("topic_id_key");

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;

    @Inject
    LoadMoreRecyclerView.Adapter mAdapter;
    private String mTopicId;
    private TopicInfo mTopicInfo;
    private MenuItem mLoveMenuItem;


    public static void openById(String topicId, Context context) {
        Navigator.from(context)
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_ID_KEY, topicId)
                .start();
    }

    public static void open(String link, Context context) {
        openById(UriUtils.getLastSegment(link), context);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_load_more_recyclerview;
    }

    @Override
    protected void startInject() {
        DaggerTopicComponent.builder()
                .appComponent(getAppComponent())
                .topicModule(new TopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTopicId = intent.getStringExtra(TOPIC_ID_KEY);
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        mToolbar.inflateMenu(R.menu.topic_info_toolbar_menu);
        mLoveMenuItem = mToolbar.getMenu().findItem(R.id.action_star);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_star:
                    if (mTopicInfo == null) {
                        toast("请等到加载完成");
                        return true;
                    }
                    if (mTopicInfo.getHeaderInfo().hadStared()) {
                        mPresenter.unStarTopic(mTopicId, mTopicInfo.getHeaderInfo().getT());
                    } else {
                        mPresenter.starTopic(mTopicId, mTopicInfo.getHeaderInfo().getT());
                    }
                    break;
                case R.id.action_thx:
                    break;
                case R.id.action_block:
                    break;
            }
            return true;
        });
    }

    @Override
    protected void init() {
        mLoadMoreRecyclerView.addDivider();
        mLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mLoadMoreRecyclerView.resetWillLoadPage();
                mPresenter.loadData(mTopicId);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, mLoadMoreRecyclerView, header);
            }
        };
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(mTopicId, willLoadPage);
    }

    @Override
    public void fillView(TopicInfo topicInfo, boolean isLoadMore) {
        mTopicInfo = topicInfo;
        if (topicInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(topicInfo.getItems(isLoadMore), isLoadMore);
        mLoadMoreRecyclerView.setHasMore(topicInfo.getTotalPage());
        toggleStar(mTopicInfo.getHeaderInfo().hadStared());
    }

    private void toggleStar(boolean isStared) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.love_checked_icon : R.drawable.love_normal_icon);
        mTopicInfo.getHeaderInfo().updateStarStatus(isStared);
    }

    @Override
    public void afterStarTopic(TopicInfo topicInfo) {
        mTopicInfo.getHeaderInfo().setFavoriteLink(topicInfo.getHeaderInfo().getFavoriteLink());
        toggleStar(mTopicInfo.getHeaderInfo().hadStared());
        toast("收藏成功");
    }

    @Override
    public void afterUnStarTopic(TopicInfo topicInfo) {
        mTopicInfo.getHeaderInfo().setFavoriteLink(topicInfo.getHeaderInfo().getFavoriteLink());
        toggleStar(mTopicInfo.getHeaderInfo().hadStared());
        toast("取消收藏成功");
    }

}
