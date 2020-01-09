package me.ghui.v2er.module.append;


import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.TopicInfo;

public class AppendTopicContract {
    public interface IView extends BaseContract.IView {
        void fillView();

        void onPostSuccess(TopicInfo topicInfo);

        void onPostFailure();
    }

    public interface IPresenter extends BaseContract.IPresenter {

        void sendAppend(String content, String topicId);

        void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo);

    }
}
