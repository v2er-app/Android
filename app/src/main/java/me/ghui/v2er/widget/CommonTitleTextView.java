package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 20/07/2017.
 */

public class CommonTitleTextView extends SizeAutoChangeTextView {
    public CommonTitleTextView(Context context) {
        super(context);
        init(context);
    }

    public CommonTitleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonTitleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (Pref.readBool(R.string.pref_key_title_overview)) {
            setMaxLines(2);
            setEllipsize(TextUtils.TruncateAt.END);
        }
    }
}
