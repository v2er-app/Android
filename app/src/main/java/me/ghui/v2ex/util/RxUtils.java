package me.ghui.v2ex.util;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2ex.network.NetError;
import me.ghui.v2ex.network.ResponseCode;
import me.ghui.v2ex.network.bean.ResponseResult;

/**
 * Created by ghui on 28/03/2017.
 */

public class RxUtils {
	private static ObservableTransformer IO_TRANSFORMER;

	public static <T> ObservableTransformer<T, T> io_main() {
		if (IO_TRANSFORMER == null) {
			IO_TRANSFORMER = new ObservableTransformer() {
				@Override
				public ObservableSource apply(Observable upstream) {
					return upstream.subscribeOn(Schedulers.io())
							.unsubscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread());
				}
			};
		}
		//noinspection unchecked
		return IO_TRANSFORMER;
	}


	public static <T> ObservableTransformer<ResponseResult<T>, T> rx(final LifecycleProvider<ActivityEvent> provider) {
		return new ObservableTransformer<ResponseResult<T>, T>() {
			@Override
			public ObservableSource<T> apply(Observable<ResponseResult<T>> upstream) {
				return upstream
						.compose(provider.<ResponseResult<T>>bindToLifecycle())
						.compose(RxUtils.<ResponseResult<T>>io_main())
						.flatMap(new Function<ResponseResult<T>, ObservableSource<? extends T>>() {
							@Override
							public ObservableSource<? extends T> apply(ResponseResult<T> t) throws Exception {
								if (t == null) {
									return Observable.error(new NullPointerException());
								} else {
									if (t.getCode() == ResponseCode.CODE_SUCCESS) {
										if (t.getData() != null) {
											return Observable.just(t.getData());
										} else {
											return Observable.empty();
										}
									}
									return Observable.error(new NetError(t.getCode(), t.getMessage()));
								}
							}
						})
						.compose(new ObservableTransformer<T, T>() {
							@Override
							public ObservableSource<T> apply(Observable<T> upstream) {
								return upstream
										.doOnSubscribe(new Consumer<Disposable>() {
											@Override
											public void accept(Disposable disposable) throws Exception {
//												loadingHandler.showLoading();
											}
										})
										.doOnComplete(new Action() {
											@Override
											public void run() throws Exception {
//												loadingHandler.hideLoading();
											}
										})
										.doOnError(new Consumer<Throwable>() {
											@Override
											public void accept(Throwable throwable) throws Exception {
//												loadingHandler.hideLoading();
											}
										});
							}
						});
			}
		};
	}
}
