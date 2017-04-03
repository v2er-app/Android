package me.ghui.v2ex.module.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by ghui on 25/03/2017.
 */

public class BaseContract {

	public interface IView {

		void showLoading();

		void hideLoading();

		<K> LifecycleTransformer<K> bindToLife();
	}

	public interface IPresenter {
		void start();
	}
}
