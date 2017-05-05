package me.ghui.v2ex.module.drawer.dailyhot;

import android.support.v7.widget.LinearLayoutManager;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.injector.component.DaggerDailyHotComponent;
import me.ghui.v2ex.injector.module.DailyHotModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.widget.BaseRecyclerView;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView {

    @BindView(R.id.recyclerview_act_daily)
    BaseRecyclerView mRecyclerView;

    @Inject
    DailyHotAdapter mDailyHotAdapter;


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
    }

    @Override
    public void fillView(DailyHotInfo dailyHotInfo) {
        mDailyHotAdapter.setData(dailyHotInfo);
    }


}
