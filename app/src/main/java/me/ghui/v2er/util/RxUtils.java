package me.ghui.v2er.util;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2er.module.base.IViewLoading;

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

    public static <K> ObservableTransformer<K, K> rxActivity(LifecycleProvider<ActivityEvent> lifecycleProvider, final IViewLoading viewLoading) {
        return upstream -> upstream
                .compose(RxUtils.io_main())
                .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(upstream1 -> upstream1.
                        doOnSubscribe(disposable -> {
                            if (viewLoading != null)
                                viewLoading.showLoading();
                        })
                        .doOnComplete(() -> {
                            if (viewLoading != null)
                                viewLoading.hideLoading();
                        })
                        .doOnError(throwable -> {
                            if (viewLoading != null)
                                viewLoading.hideLoading();
                        }));
    }


    public static <K> ObservableTransformer<K, K> rxFragment(LifecycleProvider<FragmentEvent> lifecycleProvider, final IViewLoading viewLoading) {
        return upstream -> upstream
                .compose(RxUtils.io_main())
                .compose(lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(upstream1 -> upstream1.
                        doOnSubscribe(disposable -> {
                            if (viewLoading != null)
                                viewLoading.showLoading();
                        })
                        .doOnComplete(() -> {
                            if (viewLoading != null)
                                viewLoading.hideLoading();
                        })
                        .doOnError(throwable -> {
                            if (viewLoading != null)
                                viewLoading.hideLoading();
                        }));
    }

}
