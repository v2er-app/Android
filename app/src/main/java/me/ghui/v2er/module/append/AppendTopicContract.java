package me.ghui.v2er.module.append;


import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.AppendTopicPageInfo;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.TopicInfo;

public class AppendTopicContract {
    public interface IView extends BaseContract.IView {
        void fillView(AppendTopicPageInfo pageInfo);

        void onPostSuccess(TopicInfo topicInfo);

        void onPostFailure();
    }

    public interface IPresenter extends BaseContract.IPresenter {

        void setTopicId(String id);

        void sendAppend(String content);

        void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo);

    }
}
