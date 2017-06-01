package me.ghui.v2ex.module.user;

import com.orhanobut.logger.Logger;

import me.ghui.v2ex.network.APIService;

/**
 * Created by ghui on 01/06/2017.
 */

public class UserHomePresenter implements UserHomeContract.IPresenter {

    private UserHomeContract.IView mView;

    public UserHomePresenter(UserHomeContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        APIService.get().userPageInfo(mView.getUsername())
                .compose(mView.rx())
                .subscribe(userPageInfo -> {
                    Logger.d("userPageInfo: " + userPageInfo);
                    mView.fillView(userPageInfo);
                });
    }
}
