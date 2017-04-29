package me.ghui.v2ex.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2ex.module.base.IBindToLife;
import me.ghui.v2ex.module.base.IViewLoading;

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
        return IO_TRANSFORMER;
    }

    public static <K> ObservableTransformer<K, K> rx(final IBindToLife bindToLife, final IViewLoading viewLoading) {
        return new ObservableTransformer<K, K>() {
            @Override
            public ObservableSource<K> apply(@NonNull Observable<K> upstream) {
                return upstream
                        .compose(RxUtils.<K>io_main())
                        .compose(bindToLife.<K>bindToLife())
                        .compose(new ObservableTransformer<K, K>() {
                            @Override
                            public ObservableSource<K> apply(@NonNull Observable<K> upstream) {
                                return upstream.doOnSubscribe(new Consumer<Disposable>() {
                                    @Override
                                    public void accept(@NonNull Disposable disposable) throws Exception {
                                        viewLoading.showLoading();
                                    }
                                }).doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        viewLoading.hideLoading();
                                    }
                                }).doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        viewLoading.hideLoading();
                                    }
                                });
                            }
                        });
            }
        };
    }

}
