package me.ghui.v2ex.module.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import me.ghui.v2ex.R;

/**
 * Created by ghui on 21/03/2017.
 * Fragment with a toobar
 */

public abstract class BaseBarFragment extends BaseFragment {

	@BindView(R.id.basebar)
	Toolbar mToolbar;
	@BindView(R.id.content_container)
	FrameLayout mContentLayout;

	@Override
	protected int attachLayoutId() {
		return R.layout.frag_basebar_fragment;
	}

	@LayoutRes
	protected abstract int attachContentLayoutId();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (attachContentLayoutId() != 0) {
			inflater.inflate(attachContentLayoutId(), mContentLayout);
		}
		return view;
	}
}
