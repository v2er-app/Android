package me.ghui.v2ex.module.home;

import me.ghui.v2ex.module.base.BaseContract;
import me.ghui.v2ex.module.base.BaseFragment;

/**
 * Created by ghui on 03/04/2017.
 */

public class BaseHomeFragment<T extends BaseContract.IPresenter> extends BaseFragment<T> {

	@Override
	protected int attachLayoutRes() {
		return 0;
	}

	@Override
	protected void inject() {

	}

	@Override
	protected void init() {

	}

}
