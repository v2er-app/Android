package me.ghui.v2er.widget.richtext;

import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * https://stackoverflow.com/questions/22810147/error-when-selecting-text-from-textview-java-lang-indexoutofboundsexception-se
 */

public class HackyTextView extends android.support.v7.widget.AppCompatTextView {

    public HackyTextView(Context context) {
        super(context);
    }

    public HackyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        // FIXME simple workaround to https://code.google.com/p/android/issues/detail?id=191430
        if (!isTextSelectable()) {
            return super.dispatchTouchEvent(event);
        }
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        if (startSelection < 0 || endSelection < 0) {
            try {
                Selection.setSelection((Spannable) getText(), getText().length());
            } catch (Exception e) {
                return super.dispatchTouchEvent(event);
            }
        } else if (startSelection != endSelection) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                final CharSequence text = getText();
                setText(null);
                setText(text);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
