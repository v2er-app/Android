package me.ghui.v2er.module.base;

import android.content.Context;

import io.reactivex.ObservableTransformer;

/**
 * Created by ghui on 25/03/2017.
 */

public class BaseContract {

    public interface IView extends IViewLoading {
        <K> ObservableTransformer<K, K> rx();

        <K> ObservableTransformer<K, K> rx(IViewLoading viewLoading);

        void toast(String msg);

        Context getContext();
    }

    public interface IPresenter {
        void start();
    }
}
