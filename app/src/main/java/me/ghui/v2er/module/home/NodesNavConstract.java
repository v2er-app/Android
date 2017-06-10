package me.ghui.v2er.module.home;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.NodesNavInfo;

/**
 * Created by ghui on 22/05/2017.
 */

public class NodesNavConstract {

    public interface IView extends BaseContract.IView {
        void fillView(NodesNavInfo navInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {

    }

}
