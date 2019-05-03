package me.ghui.v2er.widget.richtext;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import me.ghui.v2er.R;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.ViewUtils;

/**
 * Created by ghui on 06/07/2017.
 */

public class ImageHolderDrawable extends Drawable {

    private Paint mPaint;
    private String text;
    private Rect mRect;
    private int mBgColor;
    private int mTextColor;

    public ImageHolderDrawable(Context context, int width, int height) {
        mPaint = new Paint();
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.mediumTextSize));
        if (width == -1) {
            width = ScaleUtils.getScreenW() - ScaleUtils.dp(32);
        }
        if (height == -1) {
            height = ScaleUtils.dp(100);
        }
        mRect = new Rect(0, 0, width, height);
        setBounds(mRect);
        if (DayNightUtil.isNightMode()) {
            mBgColor = 0xFF222222;
            mTextColor = 0xFF707070;
        } else {
            mBgColor = 0xfff2f2f2;
            mTextColor = 0xffc2c2c2;
        }
    }

    public ImageHolderDrawable(Context context) {
        this(context, -1, -1);
    }

    public ImageHolderDrawable(int maxWidth, Context context) {
        this(context, maxWidth, -1);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(mBgColor);
        canvas.drawRect(mRect, mPaint);
        mPaint.setColor(mTextColor);
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
