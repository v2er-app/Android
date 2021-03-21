package me.ghui.v2er.module.drawer.care;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.injector.component.DaggerSpecialCareComponent;
import me.ghui.v2er.injector.module.SpecialCareModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.CareInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 16/05/2017.
 */

public class SpecialCareActivity extends BaseActivity<SpecialCareContract.IPresenter> implements SpecialCareContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, MultiItemTypeAdapter.OnItemClickListener {
    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @Inject
    LoadMoreRecyclerView.Adapter<CareInfo.Item> mAdapter;
    private final String KEY_CARE_INFO = KEY("care_info");
    private static final String TOPIC_CURRENT_PAGE = KEY("TOPIC_CURRENT_PAGE");
    private static final String TOPIC_PAGE_Y_POS_KEY = KEY("TOPIC_PAGE_Y_POS_KEY");
    private static final String TOPIC_Y_POS_OFFSET_KEY = KEY("topic_y_pos_offset");
    private LinearLayoutManager mLinearLayoutManager;
    private CareInfo mCareInfo;

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_load_more_recyclerview;
    }

    @Override
    protected void startInject() {
        DaggerSpecialCareComponent.builder()
                .appComponent(getAppComponent())
                .specialCareModule(new SpecialCareModule(this))
                .build().inject(this);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
    }

    @Override
    protected void init() {
        mLoadMoreRecyclerView.addDivider();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLoadMoreRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
        mCareInfo = (CareInfo) getIntent().getSerializableExtra(KEY_CARE_INFO);
        if (mCareInfo != null) {
            int page = getIntent().getIntExtra(TOPIC_CURRENT_PAGE, 1);
            // 当前加载到了mPage页
            mLoadMoreRecyclerView.setWillLoadPage(page);
            // fillView中setHasMore会根据当前列表显示条目数和总条目数计算下一次将加载的mPage
            fillView(mCareInfo, false);
            int pos = getIntent().getIntExtra(TOPIC_PAGE_Y_POS_KEY, 0);
            int offset = getIntent().getIntExtra(TOPIC_Y_POS_OFFSET_KEY, 0);
            L.d("1findFirstCompletelyVisibleItemPosition: " + pos + ", offset: " + offset);
            post(()-> mLinearLayoutManager.scrollToPositionWithOffset(pos, offset));
        }
    }

    @Override
    protected void autoLoad() {
        if (mCareInfo == null) {
            super.autoLoad();
        }
    }

    @Override
    protected void reloadMode(int mode) {
        int pos = mLinearLayoutManager.findFirstVisibleItemPosition();
        int offset = mLoadMoreRecyclerView.getChildAt(0).getTop();
        L.d("0findFirstCompletelyVisibleItemPosition: " + pos + ", top: " + offset);
        if (mCareInfo != null) {
            mCareInfo.setItems(mAdapter.getDatas());
        }
        ActivityReloader.target(this)
                .putExtra(KEY_CARE_INFO, mCareInfo)
                .putExtra(TOPIC_CURRENT_PAGE, mPresenter.getPage())
                .putExtra(TOPIC_PAGE_Y_POS_KEY, pos)
                .putExtra(TOPIC_Y_POS_OFFSET_KEY, offset)
                .reload();
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> {
            mLoadMoreRecyclerView.resetWillLoadPage();
            mPresenter.start();
        };
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadMore(willLoadPage);
    }

    @Override
    public void fillView(CareInfo careInfo, boolean isLoadMore) {
        mCareInfo = careInfo;
        if (careInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(careInfo.getItems(), isLoadMore);
        mLoadMoreRecyclerView.setHasMore(mAdapter.getContentItemCount() < careInfo.getTotal());
    }


    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        CareInfo.Item item = mAdapter.getItem(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .author(item.getUserName())
                .tag(item.getTagName())
                .commentNum(item.getComentNum())
                .build();
        TopicActivity.open(mAdapter.getDatas().get(position).getLink(), this, holder.getView(R.id.avatar_img), basicInfo);
    }
}
