package me.ghui.v2ex.module.drawer.dailyhot;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.ResponseResult;
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
				.compose(RxUtils.<ResponseResult>io_main())
				.compose(mView.<ResponseResult>bindToLife())
				.subscribe(new Observer<ResponseResult>() {
					@Override
					public void onSubscribe(Disposable d) {
						mView.showLoading();
					}

					@Override
					public void onNext(ResponseResult responseResult) {
						Logger.i(" ResponseResult :" +responseResult.toString());
					}

					@Override
					public void onError(Throwable e) {
						// TODO: 30/03/2017  
						mView.hideLoading();
					}

					@Override
					public void onComplete() {
						mView.hideLoading();
					}
				});
	}

}
