package me.ghui.v2er.module.append;

import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;

public class AppendTopicPresenter implements AppendTopicContract.IPresenter{

    private AppendTopicContract.IView mView;

    public AppendTopicPresenter(AppendTopicContract.IView view) {
        mView = view;
    }

    @Override
    public void sendAppend(String content, String topicId) {

    }

    @Override
    public void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo) {

    }

    @Override
    public void start() {

    }
}
