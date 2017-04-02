package me.ghui.v2ex.module.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import me.ghui.v2ex.general.App;
import me.ghui.v2ex.injector.component.AppComponent;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseActivity<T extends BaseContract.IBasePresenter> extends RxActivity implements BaseContract.IBaseView {

	protected ViewGroup mRootView;

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

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(onCreateRootView());
		ButterKnife.bind(this);
		initInjector();
		init();
		updateUI();
	}

	protected ViewGroup onCreateRootView() {
		if (attachLayoutRes() != 0) {
			mRootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(attachLayoutRes(), null);
		}
		return mRootView;
	}


	protected AppCompatActivity getActivity() {
		return this;
	}

	protected Context getContext() {
		return this;
	}

	protected AppComponent getAppComponent() {
		return App.get().getAppComponent();
	}

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	public <K> LifecycleTransformer<K> bindToLife() {
		return bindToLifecycle();
	}

	protected void updateUI() {
	}

	protected void delay(long millisecond, Runnable runnable) {
		mRootView.postDelayed(runnable, millisecond);
	}

	protected void post(Runnable runnable) {
		mRootView.post(runnable);
	}
}
