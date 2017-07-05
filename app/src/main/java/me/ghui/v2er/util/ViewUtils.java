package me.ghui.v2er.util;

import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by ghui on 05/07/2017.
 */

public class ViewUtils {
    public static int getExactlyWidth(View view, boolean excludePadding) {
        int width = view.getWidth();
        if (width <= 0) {
            view.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.UNSPECIFIED);
            width = view.getMeasuredWidth();
        }
        if (excludePadding) {
            width = width - view.getPaddingLeft() - view.getPaddingRight();
        }

        Logger.d("exactlyWidth: " + width);
        return width;
    }
}
