package me.ghui.v2ex.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import me.ghui.v2ex.R;
import me.ghui.v2ex.util.ResUtils;

/**
 * Created by ghui on 21/03/2017.
 */

public class BaseToolBar extends Toolbar {
	public BaseToolBar(Context context) {
		super(context);
		init();
	}

	public BaseToolBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BaseToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		this.setContentInsetStartWithNavigation(0);
		this.setBackgroundColor(ResUtils.getColor(R.color.colorPrimary, getContext()));
		this.setTitleTextColor(ResUtils.getColor(R.color.bodyTextColor, getContext()));
	}


}
