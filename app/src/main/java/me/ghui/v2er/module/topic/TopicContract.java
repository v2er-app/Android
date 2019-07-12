package me.ghui.v2er.module.topic;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.ThxResponseInfo;
import me.ghui.v2er.network.bean.TopicInfo;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicContract {
    public interface IView extends BaseContract.IView {

        boolean getScanOrder();

        String getTopicId();

        String getOnce();

        void fillView(TopicInfo topicInfo, int page);

        void afterStarTopic(TopicInfo topicInfo);

        void afterUnStarTopic(TopicInfo topicInfo);

        void afterThxCreator(boolean success);

        void afterIgnoreTopic(boolean success);

        void afterIgnoreReply(int position);

        void afterReportTopic(boolean success);

        void afterReplyTopic(TopicInfo topicInfo);

        List<TopicInfo.Item> topicReplyInfo();
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadData(String topicId, int page);

        Observable<ThxResponseInfo> thxReplier(String replyId, String t);

        void thxCreator(String id, String t);

        void starTopic(String topicId, String t);

        void unStarTopic(String topicId, String t);

        void ignoreTopic(String topicId, String once);

        void ignoreReply(int position, String replyId, String once);

        void replyTopic(String topicId, Map<String, String> replyMap);

        void reportTopic();

        int getPage();

    }
}
