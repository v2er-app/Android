package me.ghui.v2er.module.drawer.dailyhot;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerDailyHotComponent;
import me.ghui.v2er.injector.module.DailyHotModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.widget.BaseRecyclerView;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.common_recyclerview)
    BaseRecyclerView mRecyclerView;

    @Inject
    CommonAdapter<DailyHotInfo.Item> mDailyHotAdapter;


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
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.start();
            }
        };
    }

    @Override
    protected void init() {
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mDailyHotAdapter);
        mDailyHotAdapter.setOnItemClickListener(this);
    }

    @Override
    public void fillView(DailyHotInfo dailyHotInfo) {
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
        TopicActivity.openById(item.getId(), this, holder.getImgView(R.id.avatar_img), basicInfo);
    }

}
