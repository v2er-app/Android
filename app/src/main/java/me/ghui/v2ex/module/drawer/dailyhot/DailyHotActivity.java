package me.ghui.v2ex.module.drawer.dailyhot;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseBarActivity;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseBarActivity<DailyHotContract.IDailyHotPresenter> implements DailyHotContract.IDailyHotView {

	@Override
	protected int attachContentLayoutRes() {
		return R.layout.act_daily_hot;
	}

	@Override
	protected void initInjector() {
	}

	@Override
	protected void init() {

	}

}
