package me.ghui.v2er.module.create;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.IBaseInfo;
import me.ghui.v2er.network.bean.TopicInfo;

/**
 * Created by ghui on 05/06/2017.
 */

public class CreateTopicPresenter implements CreateTopicContract.IPresenter {

    private CreateTopicContract.IView mView;
    private CreateTopicPageInfo mTopicPageInfo;

    public CreateTopicPresenter(CreateTopicContract.IView iView) {
        this.mView = iView;
    }

    @Override
    public void start() {
        APIService.get().topicCreatePageInfo()
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<CreateTopicPageInfo>() {
                    @Override
                    public void onConsume(CreateTopicPageInfo createTopicPageInfo) {
                        mTopicPageInfo = createTopicPageInfo;
                        mView.fillView(createTopicPageInfo);
                    }
                });
    }

    @Override
    public void sendPost(String title, String content, String nodeId) {
        APIService.get().postTopic(mTopicPageInfo.toPostMap(title, content, nodeId))
                .compose(mView.rx())
                .map(response -> response.body().string())
                .map(s -> {
                    TopicInfo topicInfo = APIService.fruit().fromHtml(s, TopicInfo.class);
                    if (!topicInfo.isValid()) {
                        return APIService.fruit().fromHtml(s, CreateTopicPageInfo.class);
                    }
                    return topicInfo;
                })
                .subscribe(new GeneralConsumer<IBaseInfo>() {
                    @Override
                    public void onConsume(IBaseInfo info) {
                        if (info instanceof TopicInfo) {
                            //success
                            mView.onPostSuccess((TopicInfo) info);
                        } else {
                            //failure
                            mView.onPostFailure((CreateTopicPageInfo) info);
                        }
                    }
                });
    }
}
