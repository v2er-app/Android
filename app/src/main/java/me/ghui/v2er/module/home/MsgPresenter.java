package me.ghui.v2er.module.home;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NotificationInfo;

/**
 * Created by ghui on 10/05/2017.
 */

public class MsgPresenter implements MsgContract.IPresenter {

    private MsgContract.IView mView;

    public MsgPresenter(MsgContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        loadMore(1);
    }

    @Override
    public void loadMore(int page) {
        APIService.get()
                .notifications(page)
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<NotificationInfo>() {
                    @Override
                    public void onConsume(NotificationInfo info) {
                        boolean isLoadMore = page > 1;
                        mView.fillView(info, isLoadMore);
                    }
                });
    }

}
