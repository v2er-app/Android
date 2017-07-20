package me.ghui.v2er.widget.richtext;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import me.ghui.v2er.R;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.ViewUtils;

/**
 * Created by ghui on 06/07/2017.
 */

public class ImageHolderDrawable extends Drawable {

    private Paint mPaint;
    private String text;
    private Rect mRect;

    public ImageHolderDrawable(Context context, int width, int height) {
        mPaint = new Paint();
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.mediumTextSize));
        if (width == -1 || height == -1) {
            width = (int) (ScaleUtils.getScreenW((Activity) context) - ScaleUtils.dp(32));
            height = ScaleUtils.dp(100);
        }
        mRect = new Rect(0, 0, width, height);
        setBounds(mRect);
    }

    public ImageHolderDrawable(Context context) {
        this(context, -1, -1);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(0xfff2f2f2);
        canvas.drawRect(mRect, mPaint);
        mPaint.setColor(0xffc2c2c2);
        ViewUtils.drawCenterText(text, mRect, canvas, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


}
