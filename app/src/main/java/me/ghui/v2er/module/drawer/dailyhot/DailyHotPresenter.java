package me.ghui.v2er.module.drawer.dailyhot;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.DailyHotInfo;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotPresenter implements DailyHotContract.IPresenter {

    private DailyHotContract.IView mView;

    public DailyHotPresenter(DailyHotContract.IView dailyHotView) {
        this.mView = dailyHotView;
    }

    @Override
    public void start() {
        APIService.get()
                .dailyHot()
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<DailyHotInfo>() {
                    @Override
                    public void onConsume(DailyHotInfo items) {
                        mView.fillView(items);
                    }
                });
    }

}
