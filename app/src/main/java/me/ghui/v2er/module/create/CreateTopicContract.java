package me.ghui.v2er.module.create;

import java.util.List;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.TopicInfo;

/**
 * Created by ghui on 04/06/2017.
 */

public class CreateTopicContract {
    public interface IView extends BaseContract.IView {
        void fillView(CreateTopicPageInfo topicPageInfo);

        void onPostSuccess(TopicInfo topicInfo);

        void onPostFailure(CreateTopicPageInfo createTopicPageInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void sendPost(String title, String content, String nodeId);

        void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo);

        List<NodesInfo.Node> getNodes();
    }
}
