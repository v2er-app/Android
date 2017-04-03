package me.ghui.v2ex.module.base;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.widget.PtrMaterialFrameLayout;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseFragment<T extends BaseContract.IPresenter> extends RxFragment implements BaseContract.IView {

	//root view of this fragment
	protected ViewGroup mRootView;

//	@Nullable
//	private PtrMaterialFrameLayout mPtrMaterialFrameLayout;

	@Inject
	protected T mPresenter;

	/**
	 * if you want to support ptr, then attach a PtrHandler to it
	 *
	 * @return PtrHandler
	 */
	protected PtrHandler attachPtrHandler() {
		return null;
	}

	/**
	 * bind a layout resID to the content of this page
	 *
	 * @return
	 */
	@LayoutRes
	protected abstract int attachLayoutRes();

	/**
	 * init Dagger2 injector
	 */
	protected abstract void inject();

	/**
	 * init views in this page
	 */
	protected abstract void init();

	/**
	 * fetch data from server to views in this page
	 */
	protected void updateUI() {

	}


	@Nullable
	@Override
	@CallSuper
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		onCreateRootView(inflater, container);
		ButterKnife.bind(this, mRootView);
		inject();
		init();
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		return mRootView;
	}

	protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
		if (attachPtrHandler() != null) {
			if (mRootView == null) {
				mRootView = new PtrMaterialFrameLayout(getContext());
				mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
						, ViewGroup.LayoutParams.MATCH_PARENT));
				container.addView(mRootView);
				PtrMaterialFrameLayout ptrMaterialFrameLayout = (PtrMaterialFrameLayout) mRootView;
				View content = inflater.inflate(attachLayoutRes(), mRootView, false);
				ptrMaterialFrameLayout.setContentView(content);
				ptrMaterialFrameLayout.setPtrHandler(attachPtrHandler());
				ptrMaterialFrameLayout.setPinContent(true);
			}
		} else {
			if (mRootView == null) {
				mRootView = (ViewGroup) inflater.inflate(attachLayoutRes(), container, false);
			}
		}
		return mRootView;
	}

	@Nullable
	protected PtrMaterialFrameLayout getPtrLayout() {
		if (attachPtrHandler() != null) return (PtrMaterialFrameLayout) mRootView;
		else return null;
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Logger.i("onActivityCreated." + this.getClass().getSimpleName());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Logger.i("setUserVisibleHint: " + isVisibleToUser + ", " + this.getClass().getSimpleName());
		if (isVisibleToUser) {
			updateUI();
		}
	}


	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	public <T> LifecycleTransformer<T> bindToLife() {
		return bindToLifecycle();
	}
}
