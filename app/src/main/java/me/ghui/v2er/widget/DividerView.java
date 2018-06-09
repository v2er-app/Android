package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import me.ghui.toolbox.android.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.util.DayNightUtil;

/**
 * Created by ghui on 11/05/2017.
 */

public class DividerView extends View {
    public DividerView(Context context) {
        super(context);
        init(context);
    }

    public DividerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DividerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Theme.getColor(R.attr.divider_color, getContext()));
    }
}
