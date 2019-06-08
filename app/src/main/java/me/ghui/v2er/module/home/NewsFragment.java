package me.ghui.v2er.module.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.orhanobut.logger.Logger;

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
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseHomeFragment<NewsContract.IPresenter> implements NewsContract.IView,
        MultiItemTypeAdapter.OnItemClickListener,  MainActivity.ChangeTabTypeDelegate {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @Inject
    LoadMoreRecyclerView.Adapter<NewsInfo.Item> mAdapter;
    private TabInfo mCurrentTab;
    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;

    public static NewsFragment newInstance() {
        Bundle args = new Bundle();
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> {
            Logger.d("onLoadMore.willLoadPage: " + willLoadPage);

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
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> {
            mRecyclerView.resetWillLoadPage();
            mPresenter.start();
        };
    }

    @Override
    public void fillView(NewsInfo newsInfos, boolean isLoadMore) {
        if (mUpdateUnReadMsgDelegate != null) {
            mUpdateUnReadMsgDelegate.updateUnReadMsg(1, newsInfos.getUnReadCount());
        }

        List<NewsInfo.Item> items = newsInfos.getItems();
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
