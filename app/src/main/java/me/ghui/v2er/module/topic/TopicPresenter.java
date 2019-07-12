package me.ghui.v2er.module.topic;

import java.util.Map;

import io.reactivex.Observable;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.DailyInfo;
import me.ghui.v2er.network.bean.IgnoreResultInfo;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.ThxResponseInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.RefererUtils;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicPresenter implements TopicContract.IPresenter {

    private TopicContract.IView mView;
    private TopicInfo mTopicInfo;
    // 当前加载的页面
    private int mPage;

    /**
     * 当前加载的页面
     * @return
     */
    public int getPage() {
        return mPage;
    }

    public TopicPresenter(TopicContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        boolean isScanInOrder = mView.getScanOrder();
        loadData(mView.getTopicId(), isScanInOrder ? 1 : 999);
    }

    @Override
    public void loadData(String topicId, int page) {
        APIService.get().topicDetails(topicId, page)
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mPage = page;
                        mTopicInfo = topicInfo;
                        mView.fillView(topicInfo, page);
                    }
                });
    }

    @Override
    public Observable<ThxResponseInfo> thxReplier(String replyId, String once) {
        return APIService.get().thxReplier(replyId, once)
                .flatMap(simpleInfo -> APIService.get().thxMoney())
                .compose(mView.rx());
    }

    @Override
    public void thxCreator(String id, String t) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;

        APIService.get().thxCreator(id, t)
                .flatMap(simpleInfo -> APIService.get().thxMoney())
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<ThxResponseInfo>(mView) {
                    @Override
                    public void onConsume(ThxResponseInfo responseInfo) {
                        mView.afterThxCreator(responseInfo.isValid());
                    }
                });
    }


    @Override
    public void starTopic(String topicId, String t) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().starTopic(RefererUtils.topicReferer(topicId), topicId, t)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterStarTopic(topicInfo);
                    }
                });
    }

    @Override
    public void unStarTopic(String topicId, String t) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().unStarTopic(RefererUtils.topicReferer(topicId), topicId, t)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterUnStarTopic(topicInfo);
                    }
                });
    }

    @Override
    public void ignoreTopic(String topicId, String once) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().ignoreTopic(topicId, once)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NewsInfo>(mView) {
                    @Override
                    public void onConsume(NewsInfo newsInfo) {
                        // todo 可能没成功
                        mView.afterIgnoreTopic(newsInfo.isValid());
                    }
                });
    }

    @Override
    public void ignoreReply(int position, String replyId, String once) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().ignoreReply(replyId, once)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<IgnoreResultInfo>(mView) {
                    @Override
                    public void onConsume(IgnoreResultInfo result) {
                        mView.afterIgnoreReply(position);
                    }
                });
    }

    @Override
    public void replyTopic(String topicId, Map<String, String> replyMap) {
        if (UserUtils.notLoginAndProcessToLogin(false, mView.getContext())) return;
        APIService.get().replyTopic(topicId, replyMap)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.afterReplyTopic(topicInfo);
                    }
                });
    }

    @Override
    public void reportTopic() {
        String reportUrl = mTopicInfo.reportUrl();
        APIService.get().reportTopic(reportUrl)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<DailyInfo>() {
                    @Override
                    public void onConsume(DailyInfo dailyInfo) {
                        mView.afterReportTopic(dailyInfo.isValid());
                    }
                });
    }

}
