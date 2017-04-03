package me.ghui.v2ex.module.base;

import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import me.ghui.v2ex.R;

/**
 * Created by ghui on 26/03/2017.
 */

public abstract class BaseBarActivity<T extends BaseContract.IPresenter> extends BaseActivity<T> {

	@BindView(R.id.basebar)
	Toolbar mToolbar;
	FrameLayout mContainerLayout;

	@Override
	protected int attachLayoutRes() {
		return R.layout.act_basebar;
	}

	@Override
	protected ViewGroup onCreateRootView() {
		ViewGroup rootView = super.onCreateRootView();
		mContainerLayout = (FrameLayout) rootView.findViewById(R.id.content_container);
		if (attachContentLayoutRes() != 0) {
			LayoutInflater.from(this).inflate(attachContentLayoutRes(), mContainerLayout);
		}
		return rootView;
	}

	@CallSuper
	@Override
	protected void init() {
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNavigationBackClick();
			}
		});
	}

	protected void setBarTitle(CharSequence title) {
		mToolbar.setTitle(title);
	}

	protected void setBarTitle(@StringRes int strId) {
		setBarTitle(getResources().getText(strId));
	}

	protected void onNavigationBackClick() {
		this.onBackPressed();
	}

	@LayoutRes
	protected abstract int attachContentLayoutRes();

}
