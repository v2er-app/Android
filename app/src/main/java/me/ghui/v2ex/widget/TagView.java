package me.ghui.v2ex.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import me.ghui.v2ex.R;
import me.ghui.v2ex.util.ScaleUtils;

/**
 * Created by ghui on 01/04/2017.
 */

public class TagView extends TextView {

	private int mColor = 0xFFF5F5F5;
	private float mCorner;
	private Paint mPaint;

	public TagView(Context context) {
		super(context);
		init(context, null);
	}

	public TagView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TagView, 0, 0);
		try {
			mColor = a.getColor(R.styleable.TagView_tagColor, mColor);
			mCorner = a.getDimension(R.styleable.TagView_tagCorner, ScaleUtils.dp(2, context));
		} finally {
			a.recycle();
		}

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(mColor);
		mPaint.setStyle(Paint.Style.FILL);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mCorner, mCorner, mPaint);
		super.onDraw(canvas);
	}
}
