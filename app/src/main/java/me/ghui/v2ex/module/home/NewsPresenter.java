package me.ghui.v2ex.module.home;

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
//		JsoupService.get()
//				.homeNews()
//				.compose(RxUtils.<NewsInfo>io_main())
//				.compose(mView.<NewsInfo>bindToLife())
//				.toList()
//				.subscribe(new SingleObserver<List<NewsInfo>>() {
//					@Override
//					public void onSubscribe(@NonNull Disposable d) {
//						mView.showLoading();
//					}
//
//					@Override
//					public void onSuccess(@NonNull List<NewsInfo> newsInfos) {
//						mView.fillView(newsInfos);
//					}
//
//					@Override
//					public void onError(@NonNull Throwable e) {
//						mView.hideLoading();
//					}
//				});
	}

}
