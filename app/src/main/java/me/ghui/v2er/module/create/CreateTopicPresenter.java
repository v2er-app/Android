package me.ghui.v2er.module.create;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;

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
                .compose(mView.rx())
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
                .subscribe(new GeneralConsumer<CreateTopicPageInfo>() {
                    @Override
                    public void onConsume(CreateTopicPageInfo createTopicPageInfo) {
                        mView.onPostFinished(createTopicPageInfo);
                    }
                });
    }
}
