package me.ghui.v2ex.module.home;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerNewsComponent;
import me.ghui.v2ex.injector.module.NewsModule;
import me.ghui.v2ex.module.base.BaseFragment;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseFragment<NewsContract.IPresenter> implements NewsContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.news_recyclerview)
    LoadMoreRecyclerView mRecyclerView;

    @Inject
    NewsAdapter mNewsAdapter;

    public static NewsFragment newInstance() {
        Bundle args = new Bundle();
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.frag_news;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_divider));
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> {
            Logger.e("onLoadMore.willLoadPage: " + willLoadPage);
            mPresenter.loadMore(willLoadPage);
        });
        mNewsAdapter.setOnItemClickListener(this);
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
    public void fillView(NewsInfo newsInfos, boolean isLoadMore) {
        List<NewsInfo.Item> items = newsInfos.getItems();
        mNewsAdapter.setData(items, isLoadMore);
        mRecyclerView.setHasMore(true);
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        String id = mNewsAdapter.getDatas().get(position).getTopicId();
        Navigator.from(getContext())
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_ID_KEY, id)
                .start();
    }
}
