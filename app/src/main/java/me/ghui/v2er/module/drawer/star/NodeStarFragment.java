package me.ghui.v2er.module.drawer.star;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerNodeStarComponent;
import me.ghui.v2er.injector.module.NodeStarModule;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeStarInfo;
import me.ghui.v2er.widget.BaseRecyclerView;

/**
 * Created by ghui on 17/05/2017.
 */

public class NodeStarFragment extends BaseFragment<NodeStarContract.IPresenter> implements NodeStarContract.IView,
        MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;
    @Inject
    CommonAdapter<NodeStarInfo.Item> mAdapter;
    private NodeStarInfo mNodeStarInfo;
    private static final String KEY_PAGE_DATA = "page_data";
    private GridLayoutManager mGridLayoutManager;

    public static NodeStarFragment newInstance(RestoreData restoreData) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PAGE_DATA, restoreData);
        NodeStarFragment fragment = new NodeStarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NodeStarFragment newInstance() {
        Bundle args = new Bundle();
        NodeStarFragment fragment = new NodeStarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_recyclerview_layout;
    }

    @Override
    protected void startInject() {
        DaggerNodeStarComponent.builder()
                .appComponent(getAppComponent())
                .nodeStarModule(new NodeStarModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mRecyclerView.setLayoutManager(mGridLayoutManager = new GridLayoutManager(getContext(), 3));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        RestoreData restoreData = (RestoreData) getArguments().getSerializable(KEY_PAGE_DATA);
        if (restoreData != null) {
            mNodeStarInfo = restoreData.nodeStarInfo;
            fillView(mNodeStarInfo);
            post(() -> mGridLayoutManager.scrollToPositionWithOffset(restoreData.pos, restoreData.offset));
        }
    }

    @Override
    protected void lazyLoad() {
        if (mNodeStarInfo == null) {
            super.lazyLoad();
        }
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> mPresenter.start();
    }

    @Override
    public void fillView(NodeStarInfo nodeInfo) {
        mNodeStarInfo = nodeInfo;
        if (nodeInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(nodeInfo.getItems());
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        NodeStarInfo.Item item = mAdapter.getItem(position);
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setAvatar(item.getImg());
        nodeInfo.setName(item.getName());
        nodeInfo.setTitle(item.getName());
        nodeInfo.setTopics(item.getTopicNum());
        NodeTopicActivity.open(item.getLink(), getContext(),
                holder.getView(R.id.node_img),
                nodeInfo);
    }

    public RestoreData getRestoreData() {
        int pos = mGridLayoutManager.findFirstVisibleItemPosition();
        View firstChild = mRecyclerView.getChildAt(0);
        int offset = 0;
        if (firstChild != null) {
            offset = firstChild.getTop();
        }
        return new RestoreData(mNodeStarInfo, pos, offset);
    }

    public static class RestoreData implements Serializable {
        NodeStarInfo nodeStarInfo;
        public int pos;
        public int offset;

        public RestoreData(NodeStarInfo nodeStarInfo, int pos, int offset) {
            this.nodeStarInfo = nodeStarInfo;
            this.pos = pos;
            this.offset = offset;
        }
    }

}
