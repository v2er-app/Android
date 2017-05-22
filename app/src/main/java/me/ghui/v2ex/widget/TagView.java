package me.ghui.v2ex.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import me.ghui.v2ex.R;
import me.ghui.v2ex.util.ScaleUtils;

/**
 * Created by ghui on 01/04/2017.
 */

public class TagView extends AppCompatTextView implements View.OnClickListener {

    private int mColor = 0xFFF5F5F5;
    private float mCorner = 0;
    private Paint mPaint;
    private String mTagLink;

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
        int padding = ScaleUtils.dp(5, context);
        setPadding(padding * 2, padding, padding * 2, padding);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TagView, 0, 0);
        try {
            mColor = a.getColor(R.styleable.TagView_tagColor, mColor);
            mCorner = a.getDimension(R.styleable.TagView_tagCorner, mColor);
        } finally {
            a.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(TagInfo tagInfo) {
        setText(tagInfo.getTagName());
        mTagLink = tagInfo.getTagLink();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mCorner > 0) {
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), mCorner, mCorner, mPaint);
        } else {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View v) {
        // TODO: 22/05/2017 open tag info page 
    }

    public static class TagInfo {
        private String tagName;
        private String tagLink;

        private TagInfo(String tagName, String tagLink) {
            this.tagName = tagName;
            this.tagLink = tagLink;
        }

        public static TagInfo build(String tagName, String tagLink) {
            return new TagInfo(tagName, tagLink);
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTagName() {
            return tagName;
        }
    }
}
