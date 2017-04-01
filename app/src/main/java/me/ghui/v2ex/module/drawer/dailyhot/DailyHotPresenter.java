package me.ghui.v2ex.module.drawer.dailyhot;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.util.RxUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotPresenter implements DailyHotContract.IPresenter {

	private DailyHotContract.IView mView;

	public DailyHotPresenter(DailyHotContract.IView dailyHotView) {
		this.mView = dailyHotView;
	}

	@Override
	public void start() {
		APIService.get().dailyHot()
				.compose(RxUtils.<DailyHotInfo>io_main())
				.compose(mView.<DailyHotInfo>bindToLife())
				.subscribe(new Observer<DailyHotInfo>() {
					@Override
					public void onSubscribe(Disposable d) {
						mView.showLoading();
					}

					@Override
					public void onNext(DailyHotInfo dailyHotInfo) {
//						Logger.i(" DailyHotInfo :" + dailyHotInfo.toString());
					}

					@Override
					public void onError(Throwable e) {
						Logger.e(e.toString());
						mView.hideLoading();
					}

					@Override
					public void onComplete() {
						mView.hideLoading();
					}
				});
	}

}
