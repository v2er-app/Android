package me.ghui.v2er.module.drawer.care;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.CareInfo;

/**
 * Created by ghui on 27/03/2017.
 */

public class SpecialCareContract {

    public interface IView extends BaseContract.IView {

        void fillView(CareInfo careInfo, boolean isLoadMore);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadMore(int page);
    }

}
