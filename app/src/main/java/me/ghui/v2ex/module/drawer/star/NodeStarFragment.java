package me.ghui.v2ex.module.drawer.star;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerNodeStarComponent;
import me.ghui.v2ex.injector.module.NodeStarModule;
import me.ghui.v2ex.module.base.BaseFragment;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.network.bean.NodeStarInfo;
import me.ghui.v2ex.widget.BaseRecyclerView;

/**
 * Created by ghui on 17/05/2017.
 */

public class NodeStarFragment extends BaseFragment<NodeStarContract.IPresenter> implements NodeStarContract.IView,
        MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.common_recyclerview)
    BaseRecyclerView mRecyclerView;
    @Inject
    CommonAdapter<NodeStarInfo.Item> mAdapter;

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
//        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.start();
            }
        };
    }

    @Override
    public void fillView(NodeStarInfo nodeInfo) {
        if (nodeInfo == null) mAdapter.setData(null);
        mAdapter.setData(nodeInfo.getItems());
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        Navigator.from(getContext())
                .to(NodeTopicActivity.class)
                .putExtra(NodeTopicActivity.TAG_LINK_KEY,
                        mAdapter.getItem(position).getId())
                .start();
    }
}
