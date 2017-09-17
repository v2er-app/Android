package me.ghui.v2er.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 05/07/2017.
 */

public class ViewUtils {
    public static int getExactlyWidth(View view, boolean excludePadding) {
        int width = view.getWidth();
        if (width <= 0) {
            view.measure(0, 0);
            width = view.getMeasuredWidth();
        }
        if (excludePadding) {
            width = width - view.getPaddingLeft() - view.getPaddingRight();
        }

        Logger.e("exactlyWidth: " + width);
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

    public static boolean isSameImgRes(ImageView imageView, int imageResource) {
        boolean result = false;
        Context ctx = App.get();
        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }
            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    public static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

    public static void highlightCommentNum(TextView commentTV) {
        boolean highLightCommentNum = Pref.readBool(R.string.pref_key_highlight_comment_num);
        commentTV.setTypeface(highLightCommentNum ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        commentTV.setTextColor(highLightCommentNum ?
                App.get().getResources().getColor(R.color.bodyTextColor) :
                App.get().getResources().getColor(R.color.hintTextColor));
    }

}
