package me.ghui.v2er.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by ghui on 17/06/2017.
 */

public class KeyboardDetectorRelativeLayout extends RelativeLayout {

    private ArrayList<IKeyboardChanged> keyboardListener = new ArrayList<IKeyboardChanged>();

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardDetectorRelativeLayout(Context context) {
        super(context);
    }

    public void addKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.add(listener);
    }

    public void removeKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.remove(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            notifyKeyboardShown();
        } else if (actualHeight < proposedheight) {
            notifyKeyboardHidden();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyKeyboardHidden() {
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardHidden();
        }
    }

    private void notifyKeyboardShown() {
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardShown();
        }
    }

    public interface IKeyboardChanged {
        void onKeyboardShown();

        void onKeyboardHidden();
    }

}

