package me.ghui.v2ex.module.base;

/**
 * Created by ghui on 25/03/2017.
 */

public class BaseContract {

	public interface IBaseView {

		void showLoading();

		void hideLoading();

	}

	public interface IBasePresenter {
		void fetchData();
	}
}
