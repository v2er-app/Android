package me.ghui.v2ex.module.drawer.dailyhot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerDailyHotComponent;
import me.ghui.v2ex.injector.module.DailyHotModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.widget.BaseRecyclerView;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView, MultiItemTypeAdapter.OnItemClickListener {

    @BindView(R.id.recyclerview_act_daily)
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
        return R.layout.act_daily_hot;
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
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        String id = mDailyHotAdapter.getDatas().get(position).getId();
        Navigator.from(getContext())
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_ID_KEY, id)
                .start();
    }

}
