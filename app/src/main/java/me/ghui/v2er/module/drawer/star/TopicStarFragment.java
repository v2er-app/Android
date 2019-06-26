package me.ghui.v2er.module.drawer.star;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerTopicStarComponent;
import me.ghui.v2er.injector.module.TopicStarModule;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.network.bean.TopicStarInfo;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

import static me.ghui.v2er.util.Utils.KEY;

/**
 * Created by ghui on 17/05/2017.
 */

public class TopicStarFragment extends BaseFragment<TopicStarContract.IPresenter> implements TopicStarContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @Inject
    LoadMoreRecyclerView.Adapter<TopicStarInfo.Item> mAdapter;
    private static final String KEY_DATA = KEY("page_data");
    private TopicStarInfo mTopicStarInfo;
    private LinearLayoutManager mLayoutManager;

    public static TopicStarFragment newInstance(RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        TopicStarFragment fragment = new TopicStarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TopicStarFragment newInstance() {
        return newInstance(null);
    }

    public RestoreData getRestoreData() {
        int offset = mLoadMoreRecyclerView.getChildAt(0).getTop();
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        if (mTopicStarInfo != null) {
            mTopicStarInfo.setItems(mAdapter.getDatas());
        }
        return new RestoreData(mPresenter.getPage(), pos, offset, mTopicStarInfo);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_load_more_recyclerview;
    }

    @Override
    protected void startInject() {
        DaggerTopicStarComponent.builder()
                .appComponent(getAppComponent())
                .topicStarModule(new TopicStarModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mLoadMoreRecyclerView.addDivider();
        mLayoutManager = new LinearLayoutManager(getContext());
        mLoadMoreRecyclerView.setLayoutManager(mLayoutManager);
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);

        RestoreData restoreData = (RestoreData) getArguments().getSerializable(KEY_DATA);
        if (restoreData != null) {
            mTopicStarInfo = restoreData.topicStarInfo;
            mLoadMoreRecyclerView.setWillLoadPage(restoreData.page);
            fillView(mTopicStarInfo, false);
            post(()-> mLayoutManager.scrollToPositionWithOffset(restoreData.scrollPos, restoreData.scrollOffset));
            hideLoading();
        }
    }

    @Override
    public void fillView(TopicStarInfo starInfo, boolean isLoadMore) {
        mTopicStarInfo = starInfo;
        if (starInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(starInfo.getItems(), isLoadMore);
        mLoadMoreRecyclerView.setHasMore(starInfo.getTotal() > mAdapter.getContentItemCount());
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> {
            mLoadMoreRecyclerView.resetWillLoadPage();
            mPresenter.start();
        };
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadMore(willLoadPage);
    }

    @Override
    protected void lazyLoad() {
        if (mTopicStarInfo == null) {
            super.lazyLoad();
        }
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        TopicStarInfo.Item item = mAdapter.getDatas().get(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .author(item.getUserName())
                .commentNum(item.getCommentNum())
                .tag(item.getTag())
                .build();
        TopicActivity.open(item.getLink(), getContext(), holder.getView(R.id.avatar_img), basicInfo);
    }

    public static class RestoreData implements Serializable {
        public int page;
        public int scrollPos;
        public int scrollOffset;
        public TopicStarInfo topicStarInfo;

        public RestoreData(int page, int scrollPos, int scrollOffset, TopicStarInfo topicStarINfo) {
            this.page = page;
            this.scrollPos = scrollPos;
            this.scrollOffset = scrollOffset;
            this.topicStarInfo = topicStarINfo;
        }
    }
}
