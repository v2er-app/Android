package me.ghui.v2er.module.home;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 10/05/2017.
 */

public class MsgPresenter implements MsgContract.IPresenter {

    private MsgContract.IView mView;
    private int mPage = 1;

    public MsgPresenter(MsgContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        if (UserUtils.isLogin()) {
            loadMore(1);
        } else {
            mView.hideLoading();
            mView.toast("登录后才能查看消息");
        }
    }

    @Override
    public void loadMore(int page) {
        APIService.get()
                .notifications(page)
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<NotificationInfo>(mView) {
                    @Override
                    public void onConsume(NotificationInfo info) {
                        mPage = page;
                        boolean isLoadMore = page > 1;
                        mView.fillView(info, isLoadMore);
                    }
                });
    }

    @Override
    public int getPage() {
        return mPage;
    }

}
