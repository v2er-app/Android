package me.ghui.v2er.module.node;

import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeTopicInfo;
import me.ghui.v2er.network.bean.SimpleInfo;
import me.ghui.v2er.util.RefererUtils;

/**
 * Created by ghui on 27/05/2017.
 */

public class NodeTopicPresenter implements NodeTopicContract.IPresenter {

    private NodeTopicContract.IView mView;
    private NodeTopicInfo mTopicInfo;

    public NodeTopicPresenter(NodeTopicContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        APIService.get().nodeInfo(mView.nodeName())
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NodeInfo>(mView) {
                    @Override
                    public void onConsume(NodeInfo nodeInfo) {
                        if (nodeInfo.isValid()) {
                            mView.fillHeaderView(nodeInfo);
                        } else {
                            mView.toast("加载节点信息失败");
                        }
                    }
                });
        loadData(mView.initPage());
    }

    @Override
    public void loadData(int page) {
        APIService.get().nodesInfo(mView.nodeName(), page)
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<NodeTopicInfo>() {
                    @Override
                    public void onConsume(NodeTopicInfo nodesInfo) {
                        mView.fillListView(nodesInfo, page > 1 && mView.initPage() == 1);
                    }
                });
    }

    @Override
    public void starNode(String url) {
        if (PreConditions.notLoginAndProcessToLogin(mView.getContext())) return;
        APIService.get().starNode(RefererUtils.tinyReferer(), url)
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<SimpleInfo>(mView) {
                    @Override
                    public void onConsume(SimpleInfo simpleInfo) {
                        boolean forStar = url.contains("/favorite/");
                        if (forStar) {
                            mView.afterStarNode();
                        } else {
                            mView.afterUnStarNode();
                        }
                    }
                });
    }

    @Override
    public void ignoreNode() {
        APIService.get().ignoreNode(mView.nodeId(), mTopicInfo.getOnce())
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NodeTopicInfo>(mView) {
                    @Override
                    public void onConsume(NodeTopicInfo nodeTopicInfo) {
                        // TODO: 26/07/2017  
                    }
                });
    }

    @Override
    public void unIgnoreNode() {

    }

}
