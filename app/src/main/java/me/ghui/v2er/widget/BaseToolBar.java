package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import me.ghui.v2er.R;
import me.ghui.v2er.util.ResUtils;

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
        setContentInsetStartWithNavigation(0);
        setContentInsetEndWithActions(0);
        setNavigationIcon(R.drawable.ic_arrow_back_black);
        setBackgroundColor(ResUtils.getColor(R.color.colorPrimary, getContext()));
        setTitleTextColor(ResUtils.getColor(R.color.bodyTextColor, getContext()));
    }
}
