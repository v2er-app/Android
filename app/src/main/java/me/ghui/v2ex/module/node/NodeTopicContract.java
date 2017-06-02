package me.ghui.v2ex.module.node;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.network.bean.NodeInfo;
import me.ghui.v2ex.network.bean.NodesInfo;

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
