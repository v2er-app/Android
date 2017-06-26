package me.ghui.v2er.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

import me.ghui.v2er.R;


/**
 * Created by ghui on 21/03/2017.
 */

public class MaxHeightScrollView extends ScrollView {

	private int maxHeight;

	public MaxHeightScrollView(Context context) {
		super(context);
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
			//200 is a defualt value
			maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, 0);
			styledAttrs.recycle();
		}
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		requestLayout();
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (maxHeight != 0) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
