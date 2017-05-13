package me.ghui.v2ex.module.home;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.network.bean.NotificationInfo;

/**
 * Created by ghui on 10/05/2017.
 */

public class MsgContract {

    public interface IView extends BaseContract.IView {
        void fillView(NotificationInfo info, boolean isLoadMore);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadMore(int page);
    }

}
