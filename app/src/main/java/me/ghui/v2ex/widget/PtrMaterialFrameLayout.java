package me.ghui.v2ex.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.ghui.v2ex.R;
import me.ghui.v2ex.util.ScaleUtils;

/**
 * Created by ghui on 03/04/2017.
 */

public class PtrMaterialFrameLayout extends PtrFrameLayout {

	private MaterialHeader mMaterialHeader;

	public PtrMaterialFrameLayout(Context context) {
		super(context);
		init();
	}

	public PtrMaterialFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PtrMaterialFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mMaterialHeader = new MaterialHeader(getContext());
		int[] colors = getResources().getIntArray(R.array.google_colors);
		mMaterialHeader.setColorSchemeColors(colors);
		mMaterialHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
		int padding = ScaleUtils.dp(8);
		mMaterialHeader.setPadding(0, padding, 0, padding);
		setHeaderView(mMaterialHeader);
		addPtrUIHandler(mMaterialHeader);
	}

	@Override
	public void autoRefresh(final boolean atOnce, final int duration) {
		post(() -> PtrMaterialFrameLayout.super.autoRefresh(atOnce, duration));
	}

	public void setContentView(View contentView) {
		if (mContent != null && contentView != null && mContent != contentView) {
			removeView(mContent);
		}
		ViewGroup.LayoutParams lp = contentView.getLayoutParams();
		if (lp == null) {
			lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			contentView.setLayoutParams(lp);
		}
		mContent = contentView;
		addView(mContent, 0);
	}

	public MaterialHeader getHeader() {
		return mMaterialHeader;
	}

	@Override
	public void setPinContent(boolean pinContent) {
		super.setPinContent(pinContent);
	}
}
