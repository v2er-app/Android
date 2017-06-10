package me.ghui.v2er.module.drawer.star;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.NodeStarInfo;

/**
 * Created by ghui on 18/05/2017.
 */

public class NodeStarContract {
    public interface IView extends BaseContract.IView {
        void fillView(NodeStarInfo nodeInfo);
    }

    public interface IPresenter extends BaseContract.IPresenter {

    }
}
