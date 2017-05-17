package me.ghui.v2ex.module.drawer.star;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.network.bean.TopicStarInfo;

/**
 * Created by ghui on 17/05/2017.
 */

public class TopicStarContract {

    public interface IView extends BaseContract.IView {
        void fillView(TopicStarInfo starInfo, boolean isLoadMore);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadMore(int page);
    }
}
