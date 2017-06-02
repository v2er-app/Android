package me.ghui.v2ex.module.topic;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.network.bean.TopicInfo;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicContract {
    public interface IView extends BaseContract.IView {
        void fillView(TopicInfo topicInfo, boolean isLoadMore);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadData(int topicId, int page);

        void loadData(int topicId);
    }
}
