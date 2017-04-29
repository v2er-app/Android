package me.ghui.v2ex.module.base;

import io.reactivex.ObservableTransformer;

/**
 * Created by ghui on 25/03/2017.
 */

public class BaseContract {

    public interface IView extends IViewLoading {
        <K> ObservableTransformer<K, K> rx();
    }

    public interface IPresenter {
        void start();
    }
}
