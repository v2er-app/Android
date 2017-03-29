package me.ghui.v2ex.module.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.ObservableTransformer;
import me.ghui.v2ex.network.bean.ResponseResult;
import me.ghui.v2ex.util.RxUtils;

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

	/**
	 * start to update UI, such as start to fetch data
	 */
	protected void updateUI() {

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

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	protected <T> ObservableTransformer<ResponseResult<T>, T> rx() {
		return RxUtils.rx(this);
	}


}
