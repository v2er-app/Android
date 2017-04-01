package me.ghui.v2ex.module.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by ghui on 25/03/2017.
 */

public class BaseContract {

	public interface IBaseView {

		void showLoading();

		void hideLoading();

		<T> LifecycleTransformer<T> bindToLife();
	}

	public interface IBasePresenter {
		void start();
	}
}
