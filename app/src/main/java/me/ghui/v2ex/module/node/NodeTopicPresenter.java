package me.ghui.v2ex.module.node;

import com.orhanobut.logger.Logger;

import me.ghui.v2ex.network.APIService;

/**
 * Created by ghui on 27/05/2017.
 */

public class NodeTopicPresenter implements NodeTopicContract.IPresenter {

    private NodeTopicContract.IView mView;

    public NodeTopicPresenter(NodeTopicContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        // TODO: 01/06/2017  check is relealy success
        APIService.get().nodeInfo(mView.nodeName())
                .compose(mView.rx())
                .subscribe(nodeInfo -> {
                    Logger.d("nodeInfo: " + nodeInfo);
                    mView.fillHeaderView(nodeInfo);
                });
        loadData(mView.initPage());
    }

    @Override
    public void loadData(int page) {
        APIService.get().nodesInfo(mView.nodeName(), page)
                .compose(mView.rx())
                .subscribe(nodesInfo -> {
                    Logger.d("nodes: " + nodesInfo);
                    mView.fillListView(nodesInfo, page > 1 && mView.initPage() == 1);
                });
    }
}
