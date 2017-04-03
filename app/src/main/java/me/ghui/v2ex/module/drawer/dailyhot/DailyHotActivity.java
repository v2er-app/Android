package me.ghui.v2ex.module.drawer.dailyhot;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.ghui.v2ex.R;
import me.ghui.v2ex.injector.component.DaggerDailyHotComponent;
import me.ghui.v2ex.injector.module.DailyHotModule;
import me.ghui.v2ex.module.base.BaseBarActivity;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.util.ScaleUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotActivity extends BaseBarActivity<DailyHotContract.IPresenter> implements DailyHotContract.IView, PtrHandler {

	@BindView(R.id.ptr_act_daily_hot)
	PtrFrameLayout mPtrFrameLayout;
	@BindView(R.id.recyclerview_act_daily)
	RecyclerView mRecyclerView;

	@Inject
	DailyHotAdapter mDailyHotAdapter;

	@Override
	protected int attachContentLayoutRes() {
		return R.layout.act_daily_hot;
	}

	@Override
	protected void startInject() {
		DaggerDailyHotComponent.builder()
				.appComponent(getAppComponent())
				.dailyHotModule(new DailyHotModule(DailyHotActivity.this))
				.build()
				.inject(this);
	}

	@Override
	protected void init() {
		super.init();
		setBarTitle(R.string.daily_hot);
		initPullToRefresh();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
		itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recyclerview_divider));
		mRecyclerView.addItemDecoration(itemDecoration);
		mRecyclerView.setAdapter(mDailyHotAdapter);
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
		mPtrFrameLayout.setPinContent(true);
		mPtrFrameLayout.setPtrHandler(this);
		post(new Runnable() {
			@Override
			public void run() {
				mPtrFrameLayout.autoRefresh();
			}
		});
	}

	@Override
	public void fillView(DailyHotInfo dailyHotInfo) {
		mDailyHotAdapter.setData(dailyHotInfo);
	}

	@Override
	public void hideLoading() {
		mPtrFrameLayout.refreshComplete();
	}

	@Override
	public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
		return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
	}

	@Override
	public void onRefreshBegin(PtrFrameLayout frame) {
		mPresenter.start();
	}
}
