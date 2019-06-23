package me.ghui.v2er.module.node;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeTopicInfo;

/**
 * Created by ghui on 25/05/2017.
 */

public class NodeTopicContract {
    public interface IPresenter extends BaseContract.IPresenter {
        void loadData(int page);

        void starNode(String url);

        void ignoreNode();

        void unIgnoreNode();

        public int getPage();
    }

    public interface IView extends BaseContract.IView {
        String nodeName();

        String nodeId();

        int initPage();

        void fillHeaderView(NodeInfo nodeInfo);

        void fillListView(NodeTopicInfo nodeTopicInfo, boolean isLoadMore);

        void afterStarNode();

        void afterUnStarNode();

        void afterIgnoreNode();

        void afterUnIgnoreNode();

    }
}
