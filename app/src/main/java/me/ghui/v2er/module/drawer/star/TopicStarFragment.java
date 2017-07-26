package me.ghui.v2er.module.drawer.star;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
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

/**
 * Created by ghui on 17/05/2017.
 */

public class TopicStarFragment extends BaseFragment<TopicStarContract.IPresenter> implements TopicStarContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @Inject
    LoadMoreRecyclerView.Adapter<TopicStarInfo.Item> mAdapter;


    public static TopicStarFragment newInstance() {
        Bundle args = new Bundle();
        TopicStarFragment fragment = new TopicStarFragment();
        fragment.setArguments(args);
        return fragment;
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
        mLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void fillView(TopicStarInfo starInfo, boolean isLoadMore) {
        if (starInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(starInfo.getItems(), isLoadMore);
        mLoadMoreRecyclerView.setHasMore(starInfo.getTotal() > mAdapter.getContentItemCount());
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mLoadMoreRecyclerView.resetWillLoadPage();
                mPresenter.start();
            }
        };
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadMore(willLoadPage);
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
}
