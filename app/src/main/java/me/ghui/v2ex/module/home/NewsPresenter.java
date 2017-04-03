package me.ghui.v2ex.module.home;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

	private NewsContract.IView mView;

	public NewsPresenter(NewsContract.IView view) {
		mView = view;
	}

	@Override
	public void start() {
		// TODO: 03/04/2017

	}

}
