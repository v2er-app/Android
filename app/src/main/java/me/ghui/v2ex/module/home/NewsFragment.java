package me.ghui.v2ex.module.home;

import android.os.Bundle;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseFragment;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseFragment {

	public static NewsFragment newInstance() {
		Bundle args = new Bundle();
		NewsFragment fragment = new NewsFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	protected int attachLayoutRes() {
		return R.layout.frag_simple_card;
	}

	@Override
	protected void inject() {

	}

	@Override
	protected void init() {

	}

	@Override
	protected PtrHandler attachPtrHandler() {
		return new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return true;
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {

			}
		};
	}
}
