package me.ghui.v2er.network;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2er.util.Voast;

// https://stackoverflow.com/questions/43525052/rxjava2-observable-take-throws-undeliverableexception
public abstract class BaseConsumer<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onConsume(t);
    }

    public abstract void onConsume(T t);

    @Override
    public void onError(Throwable e) {
        Voast.show(e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}
