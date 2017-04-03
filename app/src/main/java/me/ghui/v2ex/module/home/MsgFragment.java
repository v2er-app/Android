package me.ghui.v2ex.module.home;

import android.os.Bundle;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseFragment;

/**
 * Created by ghui on 22/03/2017.
 */

public class MsgFragment extends BaseFragment {

	public static MsgFragment newInstance() {

		Bundle args = new Bundle();

		MsgFragment fragment = new MsgFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected int attachLayoutRes() {
		return R.layout.frag_simple_card;
	}

	@Override
	protected void initInjector() {

	}

	@Override
	protected void init() {

	}

}
