package me.ghui.v2ex.module.base;

import me.ghui.v2ex.R;

/**
 * Created by ghui on 21/03/2017.
 * Fragment with a toobar
 */

public abstract class BaseToolBarFragment extends BaseFragment {

	@Override
	protected int attachLayoutId() {
		return R.layout.frag_base_bar_fragment;
	}
}
