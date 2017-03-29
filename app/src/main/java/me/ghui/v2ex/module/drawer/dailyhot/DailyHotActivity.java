package me.ghui.v2ex.module.drawer.dailyhot;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseBarActivity;
import me.ghui.v2ex.util.ScaleUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseBarActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView {

	@BindView(R.id.ptr_act_daily_hot)
	PtrFrameLayout mPtrFrameLayout;

	@Override
	protected int attachContentLayoutRes() {
		return R.layout.act_daily_hot;
	}

	@Override
	protected void init() {
		super.init();
		initPullToRefresh();
	}

	private void initPullToRefresh() {
		final MaterialHeader header = new MaterialHeader(getContext());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		header.setColorSchemeColors(colors);
		header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		int padding = ScaleUtils.dp(8);
		header.setPadding(0, padding, 0, padding);
		header.setPtrFrameLayout(mPtrFrameLayout);
		mPtrFrameLayout.setHeaderView(header);
		mPtrFrameLayout.addPtrUIHandler(header);
	}
}
