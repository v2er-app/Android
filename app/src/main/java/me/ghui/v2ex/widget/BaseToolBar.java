package me.ghui.v2ex.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import me.ghui.v2ex.R;
import me.ghui.v2ex.util.ResUtils;
import me.ghui.v2ex.util.ScaleUtils;

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
	    setNavigationIcon(R.drawable.ic_arrow_back_black);
		setContentInsetStartWithNavigation(0);
		setBackgroundColor(ResUtils.getColor(R.color.colorPrimary, getContext()));
		setTitleTextColor(ResUtils.getColor(R.color.bodyTextColor, getContext()));
		setElevation(ScaleUtils.dp(2, getContext()));
	}


}
