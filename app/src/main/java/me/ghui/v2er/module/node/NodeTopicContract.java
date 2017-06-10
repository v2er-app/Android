package me.ghui.v2er.module.node;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodesInfo;

/**
 * Created by ghui on 25/05/2017.
 */

public class NodeTopicContract {
    public interface IPresenter extends BaseContract.IPresenter {
        void loadData(int page);
    }

    public interface IView extends BaseContract.IView {
        String nodeName();

        int initPage();

        void fillHeaderView(NodeInfo nodeInfo);

        void fillListView(NodesInfo nodesInfo, boolean isLoadMore);
    }
}
