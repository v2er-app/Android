package me.ghui.v2er.module.home;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.network.bean.ExplorePageInfo;
import me.ghui.v2er.network.bean.NodesNavInfo;

/**
 * Created by ghui on 22/05/2017.
 */

public class ExplorePresenter implements ExploreContract.IPresenter {

    private ExploreContract.IView mView;
    private ExplorePageInfo pageInfo;

    public ExplorePresenter(ExploreContract.IView view) {
        mView = view;
        pageInfo = new ExplorePageInfo();
    }

    /**
     * 请求今日热议信息
     */
    private void requestDailyHotInfo() {
        APIService.get()
                .dailyHot()
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<DailyHotInfo>(mView) {
                    @Override
                    public void onConsume(DailyHotInfo items) {
                        if (items.isValid()) {
                            pageInfo.dailyHotInfo = items;
                            mView.fillView(pageInfo);
                        }
                    }
                });
    }

    /**
     * 请求节点信息
     */
    private void requestNodesNavInfo() {
        APIService.get().nodesNavInfo()
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NodesNavInfo>(mView) {
                    @Override
                    public void onConsume(NodesNavInfo items) {
                        if (items.isValid()) {
                            pageInfo.nodesNavInfo = items;
                            mView.fillView(pageInfo);
                        }
                    }
                });
    }

    @Override
    public void start() {
        requestDailyHotInfo();
        requestNodesNavInfo();
    }

}
