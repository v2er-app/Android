package me.ghui.v2er.module.home;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerNewsComponent;
import me.ghui.v2er.injector.module.NewsModule;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.Flurry;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseHomeFragment<NewsContract.IPresenter> implements NewsContract.IView,
        MultiItemTypeAdapter.OnItemClickListener, MainActivity.ChangeTabTypeDelegate {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @Inject
    LoadMoreRecyclerView.Adapter<NewsInfo.Item> mAdapter;
    private TabInfo mCurrentTab;
    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;
    private NewsInfo mNewsInfo;
    private LinearLayoutManager mLayoutManager;

    public static NewsFragment newInstance(NewsFragment.RestoreData restoreData) {
        Bundle args = new Bundle();
        if (restoreData != null) {
            args.putSerializable(KEY_DATA, restoreData);
        }
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsFragment newInstance() {
        return newInstance(null);
    }

    public RestoreData getRestoreData() {
        if (mNewsInfo == null) {
            return null;
        }
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = 0;
        View firstChild = mRecyclerView.getChildAt(0);
        if (firstChild != null) {
            offset = firstChild.getTop();
        }
        mNewsInfo.setItems(mAdapter.getDatas());
        return new RestoreData<>(mPresenter.getPage(), pos, offset, mNewsInfo);
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
        DaggerNewsComponent.builder()
                .appComponent(getAppComponent())
                .newsModule(new NewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        mCurrentTab = TabInfo.getSelectTab();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> {
            L.d("onLoadMore.willLoadPage: " + willLoadPage);

            if (!mCurrentTab.isDefaultTab()) {
                toast("只有全部标签支持加载更多");
                mRecyclerView.setHasMore(false);
                return;
            }

            if (!UserUtils.isLogin()) {
                toast("登录后才能加载更多");
                mRecyclerView.setHasMore(false);
                return;
            }

            mPresenter.loadMore(willLoadPage);
        });

        RestoreData<NewsInfo> restoreData = (RestoreData) getArguments().getSerializable(KEY_DATA);
        if (restoreData != null) {
            mNewsInfo = restoreData.info;
            mRecyclerView.setWillLoadPage(restoreData.page);
            fillView(mNewsInfo, false);
            post(() -> mLayoutManager.scrollToPositionWithOffset(restoreData.scrollPos, restoreData.scrollOffset));
            hideLoading();
        }
    }

    @Override
    protected void lazyLoad() {
        if (mNewsInfo == null) {
            super.lazyLoad();
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
    public void fillView(NewsInfo newsInfo, boolean isLoadMore) {
        mNewsInfo = newsInfo;
        if (mUpdateUnReadMsgDelegate != null) {
            mUpdateUnReadMsgDelegate.updateUnReadMsg(1, newsInfo.getUnReadCount());
        }

        List<NewsInfo.Item> items = newsInfo.getItems();
        mAdapter.setData(items, isLoadMore);
        mRecyclerView.setHasMore(mCurrentTab.isDefaultTab());
        if (!isLoadMore) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public List<NewsInfo.Item> getNewsInfo() {
        return mAdapter.getDatas();
    }

    @Override
    public TabInfo getCurrentTab() {
        return mCurrentTab;
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        if (position < 0) {
            Flurry.capture("NewsFragment.onItemClick.postion < 0");
            return;
        }
        View shareView = holder.getView(R.id.avatar_img);
        NewsInfo.Item item = mAdapter.getDatas().get(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .author(item.getUserName())
                .tag(item.getTagName())
                .tagLink(item.getTagLink())
                .commentNum(item.getReplies())
                .build();
        TopicActivity.open(item.getLinkPath(),
                getContext(), shareView, basicInfo);
    }


    @Override
    public void changeTabType(TabInfo tabInfo) {
        mCurrentTab = tabInfo;
        mPresenter.start();
    }


}
