package me.ghui.v2ex.module.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

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
import me.ghui.v2ex.util.UserUtils;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseFragment<NewsContract.IPresenter> implements NewsContract.IView,
        MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @Inject
    NewsAdapter mNewsAdapter;

    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;

    public void setUpdateUnReadMsgDelegate(UpdateUnReadMsgDelegate updateUnReadMsgDelegate) {
        mUpdateUnReadMsgDelegate = updateUnReadMsgDelegate;
    }

    public static NewsFragment newInstance() {
        Bundle args = new Bundle();
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
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
        mNewsAdapter.setOnItemClickListener(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> {
            Logger.e("onLoadMore.willLoadPage: " + willLoadPage);
            if (!UserUtils.isLogin()) {
                Toast.makeText(getContext(), "登录后才能加载更多", Toast.LENGTH_SHORT).show();
                mRecyclerView.setHasMore(false);
                return;
            }
            mPresenter.loadMore(willLoadPage);
        });
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
//        // TODO: 24/05/2017
//        newsInfos.setUnRead(3);
        mUpdateUnReadMsgDelegate.updateUnReadMsg(1, newsInfos.getUnReadCount());

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
