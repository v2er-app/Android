package me.ghui.v2er.module.drawer.star;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.APIService;

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
        APIService.get()
                .nodeStarInfo()
                .compose(mView.rx())
                .subscribe(nodeStarInfo -> {
                    Logger.d("NodeStarInfo: " + nodeStarInfo);
                    mView.fillView(nodeStarInfo);
                });
    }

}
