package me.ghui.v2er.module.drawer.dailyhot;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.injector.component.DaggerDailyHotComponent;
import me.ghui.v2er.injector.module.DailyHotModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;
    @Inject
    CommonAdapter<DailyHotInfo.Item> mDailyHotAdapter;
    private static final String TOPIC_PAGE_Y_POS_KEY = KEY("TOPIC_PAGE_Y_POS_KEY");
    private static final String TOPIC_Y_POS_OFFSET_KEY = KEY("topic_y_pos_offset");
    private static final String TOPIC_DATA_KEY = KEY("topic_data");
    private LinearLayoutManager mLayoutManager;
    private DailyHotInfo mData;


    @Override
    protected void startInject() {
        DaggerDailyHotComponent.builder()
                .appComponent(getAppComponent())
                .dailyHotModule(new DailyHotModule(DailyHotActivity.this))
                .build()
                .inject(this);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_recyclerview_layout;
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> mPresenter.start();
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
    }

    @Override
    protected void init() {
        mRecyclerView.addDivider();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDailyHotAdapter);
        mDailyHotAdapter.setOnItemClickListener(this);
        DailyHotInfoWrapper dailyHotInfoWrapper = (DailyHotInfoWrapper) getIntent().getSerializableExtra(TOPIC_DATA_KEY);
        if (dailyHotInfoWrapper != null) {
            mData = dailyHotInfoWrapper.dailyHotInfo;
            fillView(mData);
            int pos = getIntent().getIntExtra(TOPIC_PAGE_Y_POS_KEY, 0);
            int offset = getIntent().getIntExtra(TOPIC_Y_POS_OFFSET_KEY, 0);
            L.d("1findFirstCompletelyVisibleItemPosition: " + pos + ", offset: " + offset);
            post(()-> mLayoutManager.scrollToPositionWithOffset(pos, offset));
        }
    }

    @Override
    protected void reloadMode(int mode) {
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = mRecyclerView.getChildAt(0).getTop();
        ActivityReloader.target(this)
                .putExtra(TOPIC_PAGE_Y_POS_KEY, pos)
                .putExtra(TOPIC_Y_POS_OFFSET_KEY, offset)
                .putExtra(TOPIC_DATA_KEY, DailyHotInfoWrapper.create(mData))
                .reload();
    }

    @Override
    public void fillView(DailyHotInfo dailyHotInfo) {
        mData = dailyHotInfo;
        mDailyHotAdapter.setData(dailyHotInfo);
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        DailyHotInfo.Item item = mDailyHotAdapter.getDatas().get(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getMember().getAvatar())
                .author(item.getMember().getUserName())
                .tag(item.getNode().getTitle())
                .commentNum(item.getReplies())
                .build();
        TopicActivity.openById(item.getId(), this, holder.getView(R.id.avatar_img), basicInfo);
    }


}
