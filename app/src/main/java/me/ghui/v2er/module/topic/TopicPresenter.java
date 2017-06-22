package me.ghui.v2er.module.topic;

import com.orhanobut.logger.Logger;

import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.MissionInfo;
import me.ghui.v2er.network.bean.SimpleInfo;
import me.ghui.v2er.network.bean.ThxResponseInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.RefererUtils;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicPresenter implements TopicContract.IPresenter {

    private TopicContract.IView mView;

    public TopicPresenter(TopicContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
    }

    @Override
    public void loadData(String topicId, int page) {
        APIService.get().topicDetails(topicId, page)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>() {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        Logger.d("topicInfo: " + topicInfo);
                        mView.fillView(topicInfo, page > 1);
                    }
                });
    }

    @Override
    public void loadData(String topicId) {
        loadData(topicId, 1);
    }

    @Override
    public Observable<ThxResponseInfo> thxReplier(String replyId, String t) {
        return APIService.get().thxReplier(replyId, t)
                .flatMap(simpleInfo -> APIService.get().thxMoney())
                .compose(mView.rx());
    }

    @Override
    public void thxCreator(String id, String t) {
        APIService.get().thxCreator(id, t)
                .flatMap(simpleInfo -> APIService.get().thxMoney())
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<ThxResponseInfo>() {
                    @Override
                    public void onConsume(ThxResponseInfo responseInfo) {
                        mView.afterThxCreator(responseInfo.isValid());
                    }
                });
    }


    @Override
    public void starTopic(String topicId, String t) {
        APIService.get().starTopic(RefererUtils.topicReferer(topicId), topicId, t)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>() {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterStarTopic(topicInfo);
                    }
                });
    }

    @Override
    public void unStarTopic(String topicId, String t) {
        APIService.get().unStarTopic(RefererUtils.topicReferer(topicId), topicId, t)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>() {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterUnStarTopic(topicInfo);
                    }
                });
    }

    @Override
    public void ignoreTopic(String topicId, String once) {
        APIService.get().ignoreTopic(RefererUtils.tinyReferer(), topicId, once)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<MissionInfo>() {
                    @Override
                    public void onConsume(MissionInfo missionInfo) {
                        mView.afterIgnoreTopic(missionInfo.isValid());
                    }
                });
    }

    @Override
    public void ignoreReply(int position, String replyId, String once) {
        APIService.get().ignoreReply(replyId, once)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<SimpleInfo>() {
                    @Override
                    public void onConsume(SimpleInfo simpleInfo) {
                        mView.afterIgnoreReply(simpleInfo, position);
                    }
                });
    }

    @Override
    public void replyTopic(String topicId, Map<String, String> replyMap) {
        APIService.get().replyTopic(topicId, replyMap)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>() {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterReplyTopic(topicInfo);
                    }
                });
    }

}
