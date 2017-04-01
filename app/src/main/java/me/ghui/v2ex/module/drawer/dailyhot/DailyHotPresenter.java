package me.ghui.v2ex.module.drawer.dailyhot;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.ResponseWrapper;
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
	public void fetchData() {
		APIService.get().dailyHot()
				.compose(RxUtils.<ResponseWrapper>io_main())
				.compose(mView.<ResponseWrapper>bindToLife())
				.subscribe(new Observer<ResponseWrapper>() {
					@Override
					public void onSubscribe(Disposable d) {
						mView.showLoading();
					}

					@Override
					public void onNext(ResponseWrapper responseResult) {
						Logger.i(" ResponseResult :" + responseResult.toString());
					}

					@Override
					public void onError(Throwable e) {
						// TODO: 30/03/2017
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
