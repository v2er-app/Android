package me.ghui.v2ex.module.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutResId = attachLayoutRes();
		if (layoutResId != 0) {
			setContentView(layoutResId);
			ButterKnife.bind(this);
		}
		init();
	}

	/**
	 * bind a layout resID to this page
	 *
	 * @return resID
	 */
	@LayoutRes
	protected abstract int attachLayoutRes();

	protected void init() {

	}

	protected AppCompatActivity getActivity() {
		return this;
	}

	protected Context getContext() {
		return this;
	}
}
