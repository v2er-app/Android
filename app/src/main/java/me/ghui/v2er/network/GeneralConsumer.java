package me.ghui.v2er.network;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2er.util.Voast;

/**
 * Created by ghui on 19/06/2017.
 */

public abstract class GeneralConsumer<T> implements Observer<T> {

    private IGeneralErrorHandler mGeneralErrorHandler;

    public GeneralConsumer(IGeneralErrorHandler generalErrorHandler) {
        mGeneralErrorHandler = generalErrorHandler;
    }

    public GeneralConsumer() {
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        Logger.d("API RESPONSE: " + t.toString());
        onConsume(t);
    }

    public abstract void onConsume(T t);

    @Override
    public void onError(Throwable e) {
        if (mGeneralErrorHandler == null) {
            Voast.show(e.getMessage());
        } else {
            int errorCode = -1;
            mGeneralErrorHandler.handleError(errorCode, e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }
}
