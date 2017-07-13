package me.ghui.v2er.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.general.PreConditions;

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

    public static void drawCenterText(String text, Rect rect, Canvas canvas, Paint paint) {
        if (PreConditions.isEmpty(text)) return;
        Paint.Align align = paint.getTextAlign();
        float x;
        float y;
        //x
        if (align == Paint.Align.LEFT) {
            x = rect.centerX() - paint.measureText(text) / 2;
        } else if (align == Paint.Align.CENTER) {
            x = rect.centerX();
        } else {
            x = rect.centerX() + paint.measureText(text) / 2;
        }
        //y
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float acent = Math.abs(metrics.ascent);
        float descent = Math.abs(metrics.descent);
        y = rect.centerY() + (acent - descent) / 2f;
        canvas.drawText(text, x, y, paint);
    }

}
