package me.ghui.v2er.module.home;

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
import me.ghui.v2er.injector.component.DaggerMsgComponent;
import me.ghui.v2er.injector.module.MsgModule;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 10/05/2017.
 */

public class MsgFragment extends BaseFragment<MsgContract.IPresenter>
        implements MsgContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mRecyclerView;

    @Inject
    LoadMoreRecyclerView.Adapter<NotificationInfo.Reply> mAdapter;
    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;

    public void setUpdateUnReadMsgDelegate(UpdateUnReadMsgDelegate updateUnReadMsgDelegate) {
        mUpdateUnReadMsgDelegate = updateUnReadMsgDelegate;
    }

    public static MsgFragment newInstance() {
        Bundle args = new Bundle();
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_load_more_recyclerview;
    }

    @Override
    protected void startInject() {
        DaggerMsgComponent.builder()
                .appComponent(getAppComponent())
                .msgModule(new MsgModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addDivider();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> mPresenter.loadMore(willLoadPage));
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mRecyclerView.resetWillLoadPage();
                mPresenter.start();
            }
        };
    }

    @Override
    public void fillView(NotificationInfo info, boolean isLoadMore) {
        if (info == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(info.getReplies(), isLoadMore);
        mRecyclerView.setHasMore(info.getPage());
        if (mUpdateUnReadMsgDelegate != null) {
            mUpdateUnReadMsgDelegate.updateUnReadMsg(1, 0);
        }
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        TopicActivity.open(mAdapter.getDatas().get(position).getLink(), getContext());
    }
}
