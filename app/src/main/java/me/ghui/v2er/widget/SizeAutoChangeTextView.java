package me.ghui.v2er.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import me.ghui.v2er.util.FontSizeUtil;

public class SizeAutoChangeTextView extends AppCompatTextView {

    public SizeAutoChangeTextView(Context context) {
        super(context);
        init();
    }

    public SizeAutoChangeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SizeAutoChangeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float size = FontSizeUtil.getContentSize();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

}
