package me.ghui.v2er.module.home;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.injector.component.DaggerExploreComponent;
import me.ghui.v2er.injector.module.ExploreModule;
import me.ghui.v2er.network.bean.ExplorePageInfo;
import me.ghui.v2er.network.bean.ExplorePageInfoWrapper;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.widget.BaseRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class ExploreFragment extends BaseHomeFragment<ExploreContract.IPresenter> implements ExploreContract.IView {

    @Inject
    MultiItemTypeAdapter<Serializable> mAdapter;
    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;

    private ExplorePageInfoWrapper mExplorePageInfoWrapper;
    private LinearLayoutManager mLayoutManager;

    public static ExploreFragment newInstance(RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        ExploreFragment fragment = new ExploreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ExploreFragment newInstance() {
        return newInstance(null);
    }

    public RestoreData<ExplorePageInfoWrapper> getRestoreData() {
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = 0;
        View firstChild = mRecyclerView.getChildAt(0);
        if (firstChild != null) {
            offset = firstChild.getTop();
        }
        if (mExplorePageInfoWrapper == null) {
            return null;
        }
        return new RestoreData<>(1, pos, offset, mExplorePageInfoWrapper);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_recyclerview_layout;
    }

    @Override
    protected void startInject() {
        DaggerExploreComponent.builder()
                .appComponent(getAppComponent())
                .exploreModule(new ExploreModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        RestoreData<ExplorePageInfoWrapper> restoreData = (RestoreData) getArguments().getSerializable(KEY_DATA);
        if (restoreData != null) {
            mExplorePageInfoWrapper = restoreData.info;
            fillView(mExplorePageInfoWrapper.explorePageInfo);
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
        if (mExplorePageInfoWrapper == null || mExplorePageInfoWrapper.explorePageInfo == null) {
            super.lazyLoad();
        }
    }

    @Override
    public void fillView(ExplorePageInfo pageInfo) {
        mExplorePageInfoWrapper = ExplorePageInfoWrapper.wrapper(pageInfo);
        pageInfo.setDailyHotInfoTitle(getString(R.string.daily_hot));
        pageInfo.setNodesNavInfoTitle(getString(R.string.node_navigation));
        mAdapter.setData(pageInfo.getItems());
    }

}
