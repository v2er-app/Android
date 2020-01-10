package me.ghui.v2er.module.append;

import android.text.TextUtils;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.AppendTopicPageInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.RefererUtils;
import me.ghui.v2er.util.Voast;

public class AppendTopicPresenter implements AppendTopicContract.IPresenter {

    private AppendTopicContract.IView mView;
    private String topicID;
    private AppendTopicPageInfo pageInfo;

    public AppendTopicPresenter(AppendTopicContract.IView view) {
        mView = view;
    }

    @Override
    public void setTopicId(String id) {
        this.topicID = id;
    }

    @Override
    public void restoreData(AppendTopicPageInfo createTopicPageInfo, String topicID) {
        pageInfo = createTopicPageInfo;
        this.topicID = topicID;
    }

    @Override
    public void start() {
        if (TextUtils.isEmpty(topicID)) {
            Voast.show("无法获得有效的帖子id");
            return;
        }
        APIService.get()
                .appendPageInfo(RefererUtils.topicReferer(topicID), topicID)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<AppendTopicPageInfo>() {
                    @Override
                    public void onConsume(AppendTopicPageInfo appendTopicPageInfo) {
                        pageInfo = appendTopicPageInfo;
//                        L.d(appendTopicPageInfo.toString());
                        mView.fillView(appendTopicPageInfo);
                    }
                });

    }

    @Override
    public void sendAppend(String content) {
        APIService.get()
                .appendTopic(topicID, pageInfo.toPostMap(content))
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.onAfterAppendTopic(topicInfo);
                    }
                });
    }

}
