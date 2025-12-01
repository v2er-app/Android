package me.ghui.v2er.module.home;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerMsgComponent;
import me.ghui.v2er.injector.module.MsgModule;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.widget.HackRecyclerView;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 10/05/2017.
 */

public class MsgFragment extends BaseHomeFragment<MsgContract.IPresenter>
        implements MsgContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    HackRecyclerView mRecyclerView;

    @Inject
    LoadMoreRecyclerView.Adapter<NotificationInfo.Reply> mAdapter;
    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;
    private NotificationInfo mNotificationInfo;
    private LinearLayoutManager mLayoutManager;


    public static MsgFragment newInstance(RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MsgFragment newInstance() {
        return newInstance(null);
    }

    public RestoreData<NotificationInfo> getRestoreData() {
        if (mNotificationInfo == null) {
            return null;
        }
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = 0;
        View firstChild = mRecyclerView.getChildAt(0);
        if (firstChild != null) {
            offset = firstChild.getTop();
        }
        mNotificationInfo.setReplies(mAdapter.getDatas());
        return new RestoreData<>(mPresenter.getPage(), pos, offset, mNotificationInfo);
    }

    public void setUpdateUnReadMsgDelegate(UpdateUnReadMsgDelegate updateUnReadMsgDelegate) {
        mUpdateUnReadMsgDelegate = updateUnReadMsgDelegate;
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
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        mRecyclerView.addDivider();
        mRecyclerView.setAdapter(mAdapter);
        // Set AppBarTracking to fix scroll behavior on some devices
        if (getActivity() instanceof HackRecyclerView.AppBarTracking) {
            mRecyclerView.setAppBarTracking((HackRecyclerView.AppBarTracking) getActivity());
        }
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> mPresenter.loadMore(willLoadPage));

        RestoreData<NotificationInfo> restoreData = (RestoreData) getArguments().getSerializable(KEY_DATA);
        if (restoreData != null) {
            mNotificationInfo = restoreData.info;
            mRecyclerView.setWillLoadPage(restoreData.page);
            fillView(mNotificationInfo, false);
            post(()-> mLayoutManager.scrollToPositionWithOffset(restoreData.scrollPos, restoreData.scrollOffset));
            hideLoading();
        }
    }

    @Override
    protected void lazyLoad() {
        if (mNotificationInfo == null) {
            if (UserUtils.isLogin()) {
                super.lazyLoad();
            } else {
                mPresenter.start();
            }
        }
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> {
            mRecyclerView.resetWillLoadPage();
            mPresenter.start();
        };
    }

    @Override
    public void fillView(NotificationInfo info, boolean isLoadMore) {
        mNotificationInfo = info;
        if (info == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(info.getReplies(), isLoadMore);
        mRecyclerView.setHasMore(mAdapter.getContentItemCount() < info.getTotal());
        if (mUpdateUnReadMsgDelegate != null) {
            mUpdateUnReadMsgDelegate.updateUnReadMsg(1, 0);
        }
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        NotificationInfo.Reply item = mAdapter.getDatas().get(position);
        TopicActivity.openWithAutoScroll(item.getLink(), getContext(), item.getContent());
    }

}
