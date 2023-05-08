package me.ghui.v2er.module.home;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.ExplorePageInfo;
import me.ghui.v2er.network.bean.NodesNavInfo;

/**
 * Created by ghui on 22/05/2017.
 */

public class ExploreContract {

    public interface IView extends BaseContract.IView {
        void fillView(ExplorePageInfo pageInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {

    }

}
