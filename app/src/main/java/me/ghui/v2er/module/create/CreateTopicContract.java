package me.ghui.v2er.module.create;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;

/**
 * Created by ghui on 04/06/2017.
 */

public class CreateTopicContract {
    public interface IView extends BaseContract.IView {
        void fillView(CreateTopicPageInfo topicPageInfo);

        void onPostFinished(CreateTopicPageInfo resultInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void sendPost(String title, String content, String nodeId);
    }
}
