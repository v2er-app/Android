package me.ghui.v2ex.module.home;

import com.orhanobut.logger.Logger;

import me.ghui.v2ex.network.APIService;

/**
 * Created by ghui on 22/05/2017.
 */

public class NodesNavPresenter implements NodesNavConstract.IPresenter {

    private NodesNavConstract.IView mView;

    public NodesNavPresenter(NodesNavConstract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        APIService.get().nodesNavInfo()
                .compose(mView.rx())
                .subscribe(items -> {
                    Logger.d("NodesNavInfo: " + items);
                    mView.fillView(items);
                });
    }

}
