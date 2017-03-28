package me.ghui.v2ex.module.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseActivity<T extends BaseContract.IBasePresenter> extends AppCompatActivity implements BaseContract.IBaseView {

	@Inject
	protected T mPresenter;

	/**
	 * bind a layout resID to the content of this page
	 *
	 * @return layout res id
	 */
	@LayoutRes
	protected abstract int attachLayoutRes();

	/**
	 * init Dagger2 injector
	 */
	protected void initInjector() {

	}

	/**
	 * init views in this page
	 */
	protected void init() {

	}

	/**
	 * start to update UI, such as start to fetch data
	 */
	protected void updateUI() {

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutResId = attachLayoutRes();
		if (layoutResId != 0) {
			setContentView(layoutResId);
			ButterKnife.bind(this);
		}
		initInjector();
		init();
		updateUI();
	}


	protected AppCompatActivity getActivity() {
		return this;
	}

	protected Context getContext() {
		return this;
	}

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}
}
