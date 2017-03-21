package me.ghui.v2ex.module.base;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseFragment extends Fragment {

	protected ViewGroup mBaseRootView;

	/**
	 * bind a layout resID to the content of this page
	 *
	 * @return
	 */
	@LayoutRes
	protected abstract int attachLayoutId();

	/**
	 * init Dagger2 injector
	 */
	protected abstract void initInjector();

	/**
	 * init views in this page
	 */
	protected abstract void initViews();

	/**
	 * fill data to views in this page
	 */
	protected abstract void fetchData();


	@Nullable
	@Override
	@CallSuper
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mBaseRootView == null) {
			mBaseRootView = (ViewGroup) inflater.inflate(attachLayoutId(), null);
			ButterKnife.bind(this, mBaseRootView);
			initInjector();
			initViews();
		}
		ViewGroup parent = (ViewGroup) mBaseRootView.getParent();
		if (parent != null) {
			parent.removeView(mBaseRootView);
		}
		return mBaseRootView;
	}


}
