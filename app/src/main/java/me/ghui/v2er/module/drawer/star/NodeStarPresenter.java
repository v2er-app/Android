package me.ghui.v2er.module.drawer.star;

import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NodeStarInfo;

/**
 * Created by ghui on 18/05/2017.
 */

public class NodeStarPresenter implements NodeStarContract.IPresenter {

    private NodeStarContract.IView mView;

    public NodeStarPresenter(NodeStarContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        if (PreConditions.notLoginAndProcessToLogin(true, mView.getContext())) return;
        APIService.get()
                .nodeStarInfo()
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NodeStarInfo>(mView) {
                    @Override
                    public void onConsume(NodeStarInfo nodeStarInfo) {
                        mView.fillView(nodeStarInfo);
                    }
                });
    }

}
