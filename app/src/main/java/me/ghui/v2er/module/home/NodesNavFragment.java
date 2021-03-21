package me.ghui.v2er.module.home;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.injector.component.DaggerNodesNavComponent;
import me.ghui.v2er.injector.module.NodesNavModule;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.network.bean.NodesNavInfoWrapper;
import me.ghui.v2er.widget.BaseRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NodesNavFragment extends BaseHomeFragment<NodesNavConstract.IPresenter> implements NodesNavConstract.IView {

    @Inject
    CommonAdapter<NodesNavInfo.Item> mAdapter;
    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;
    private NodesNavInfoWrapper mNodesNavInfoWrapper;
    private LinearLayoutManager mLayoutManager;

    public static NodesNavFragment newInstance(RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        NodesNavFragment fragment = new NodesNavFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NodesNavFragment newInstance() {
        return newInstance(null);
    }

    public RestoreData<NodesNavInfoWrapper> getRestoreData() {
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = 0;
        View firstChild = mRecyclerView.getChildAt(0);
        if (firstChild != null) {
            offset = firstChild.getTop();
        }
        if (mNodesNavInfoWrapper == null) {
            return null;
        }
        return new RestoreData<>(1, pos, offset, mNodesNavInfoWrapper);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_recyclerview_layout;
    }

    @Override
    protected void startInject() {
        DaggerNodesNavComponent.builder()
                .appComponent(getAppComponent())
                .nodesNavModule(new NodesNavModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        RestoreData<NodesNavInfoWrapper> restoreData = (RestoreData) getArguments().getSerializable(KEY_DATA);
        if (restoreData != null) {
            mNodesNavInfoWrapper = restoreData.info;
            fillView(restoreData.info.nodesNavInfo);
            post(()-> mLayoutManager.scrollToPositionWithOffset(restoreData.scrollPos, restoreData.scrollOffset));
            hideLoading();
        }
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> mPresenter.start();
    }

    @Override
    protected void lazyLoad() {
        if (mNodesNavInfoWrapper == null || mNodesNavInfoWrapper.nodesNavInfo == null) {
            super.lazyLoad();
        }
    }

    @Override
    public void fillView(NodesNavInfo navInfo) {
        mNodesNavInfoWrapper = NodesNavInfoWrapper.wrapper(navInfo);
        mAdapter.setData(navInfo);
    }
}
