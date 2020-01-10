package me.ghui.v2er.module.append;

import android.text.TextUtils;

import java.util.HashMap;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.AppendTopicPageInfo;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;
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
    public void sendAppend(String content) {
        APIService.get()
                .appendTopic(topicID, pageInfo.toPostMap(content))
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>() {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.onPostSuccess(topicInfo);
                    }
                });
    }

    @Override
    public void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo) {

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
                        L.d(appendTopicPageInfo.toString());
                        mView.fillView(appendTopicPageInfo);
                    }
                });

    }

}
