package me.ghui.v2er.module.drawer.care;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.CareInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class SpecialCarePresenter implements SpecialCareContract.IPresenter {

    private SpecialCareContract.IView mView;

    public SpecialCarePresenter(SpecialCareContract.IView spcialCareView) {
        this.mView = spcialCareView;
    }

    @Override
    public void start() {
        loadMore(1);
    }

    @Override
    public void loadMore(int page) {
        if (UserUtils.notLoginAndProcessToLogin(true, mView.getContext())) return;
        APIService.get().specialCareInfo(page)
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<CareInfo>(mView) {
                    @Override
                    public void onConsume(CareInfo careInfo) {
                        mView.fillView(careInfo, page > 1);
                    }
                });
    }
}
