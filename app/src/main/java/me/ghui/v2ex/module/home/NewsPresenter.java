package me.ghui.v2ex.module.home;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.util.RxUtils;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

	private NewsContract.IView mView;

	public NewsPresenter(NewsContract.IView view) {
		mView = view;
	}

	@Override
	public void start() {
		APIService.get()
				.homeNews("all")
				.compose(RxUtils.<NewsInfo>io_main())
				.compose(mView.<NewsInfo>bindToLife())
				.toList()
				.subscribe(new SingleObserver<List<NewsInfo>>() {
					@Override
					public void onSubscribe(@NonNull Disposable d) {
						mView.showLoading();
					}

					@Override
					public void onSuccess(@NonNull List<NewsInfo> newsInfos) {
						mView.fillView(newsInfos);
						mView.hideLoading();
					}

					@Override
					public void onError(@NonNull Throwable e) {
						mView.hideLoading();
					}
				});
	}

}
