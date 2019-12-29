package me.ghui.v2er.module.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 05/06/2017.
 */

public class CreateTopicPresenter implements CreateTopicContract.IPresenter {

    private CreateTopicContract.IView mView;
    private CreateTopicPageInfo mTopicPageInfo;
    private List<NodesInfo.Node> mNodesInfo;

    @Override
    public void restoreData(CreateTopicPageInfo createTopicPageInfo, NodesInfo nodesInfo) {
        mTopicPageInfo = createTopicPageInfo;
        mNodesInfo = nodesInfo;
    }

    @Override
    public List<NodesInfo.Node> getNodes() {
        return mNodesInfo;
    }

    public CreateTopicPresenter(CreateTopicContract.IView iView) {
        this.mView = iView;
    }

    @Override
    public void start() {
        if (UserUtils.notLoginAndProcessToLogin(true, mView.getContext())) {
            return;
        }
        // TODO: 2019-12-29 考虑加个缓存

        APIService.get()
                .nodes()
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NodesInfo>() {
                    @Override
                    public void onConsume(NodesInfo nodesInfo) {
                        // TODO: 2019-12-28 ugly here
                        mNodesInfo = nodesInfo;
                        APIService.get().topicCreatePageInfo()
                                .compose(mView.rx(null))
                                .subscribe(new GeneralConsumer<CreateTopicPageInfo>(mView) {
                                    @Override
                                    public void onConsume(CreateTopicPageInfo createTopicPageInfo) {
                                        mTopicPageInfo = createTopicPageInfo;
                                        cleanNodes();
                                        mView.fillView(createTopicPageInfo);
                                    }
                                });
                    }
                });
    }

    private void cleanNodes() {
        LinkedHashSet<String> hots = mTopicPageInfo.getHotNodeIds();
        for (NodesInfo.Node node : mNodesInfo) {
            if (hots.contains(node.id)) {
                node.isHot = true;
            }
        }
        Collections.sort(mNodesInfo);

        // rerange hot nodes
        int i = 0;
        for (String hotId : hots) {
            int index1 = findIndex(hotId);
            if (index1 != i) Collections.swap(mNodesInfo, index1, i);
            i++;
        }
    }

    private int findIndex(String id) {
        for (int i = 0; i < mNodesInfo.size(); i++) {
            if (mNodesInfo.get(i).id.equals(id)) return i;
        }
        return -1;
    }


    @Override
    public void sendPost(String title, String content, String nodeId) {
        // replace \n to \n\n
        if (Check.notEmpty(content)) {
            content = content.replaceAll("\n", "\n\n");
        }
        APIService.get().postTopic(mTopicPageInfo.toPostMap(title, content, nodeId))
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<TopicInfo>(mView) {
                    @Override
                    public void onConsume(TopicInfo topicInfo) {
                        mView.onPostSuccess(topicInfo);
                    }
                });
    }

}
