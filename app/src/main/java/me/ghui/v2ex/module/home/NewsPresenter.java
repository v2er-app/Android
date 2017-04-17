package me.ghui.v2ex.module.home;

import io.reactivex.Observer;
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
				.subscribe(new Observer<NewsInfo>() {
					@Override
					public void onSubscribe(Disposable d) {
						mView.showLoading();
					}

					@Override
					public void onNext(NewsInfo newsInfo) {
						mView.fillView(newsInfo);
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
						mView.hideLoading();
					}

					@Override
					public void onComplete() {
						mView.hideLoading();
					}
				});
	}

}
