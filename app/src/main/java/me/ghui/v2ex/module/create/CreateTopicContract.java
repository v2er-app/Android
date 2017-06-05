package me.ghui.v2ex.module.create;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.network.bean.CreateTopicPageInfo;
import me.ghui.v2ex.network.bean.TopicCreateResultInfo;

/**
 * Created by ghui on 04/06/2017.
 */

public class CreateTopicContract {
    public interface IView extends BaseContract.IView {
        void fillView(CreateTopicPageInfo topicPageInfo);

        void onPostFinished(TopicCreateResultInfo resultInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void sendPost(String title, String content, String nodeId);
    }
}
