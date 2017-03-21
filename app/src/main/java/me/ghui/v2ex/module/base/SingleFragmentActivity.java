package me.ghui.v2ex.module.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2ex.R;

/**
 * Created by ghui on 21/03/2017.
 */

public abstract class SingleFragmentActivity extends BaseActivity {

	@Override
	protected int attachLayoutRes() {
		return R.layout.act_single_fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseFragment baseFragment = createFragment();
		if (baseFragment != null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.root_act_single_fragment, baseFragment)
					.commit();
		}
	}

	protected abstract BaseFragment createFragment();
}
