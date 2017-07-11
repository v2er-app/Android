package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.ghui.v2er.R;
import me.ghui.v2er.util.ResUtils;

/**
 * Created by ghui on 21/03/2017.
 */

public class BaseToolBar extends Toolbar {
    public BaseToolBar(Context context) {
        super(context);
        init();
    }

    public BaseToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContentInsetStartWithNavigation(0);
        setContentInsetEndWithActions(0);
        setNavigationIcon(R.drawable.ic_arrow_back_black);
//        setTitleTextColor(ResUtils.getColor(R.color.bodyTextColor, getContext()));
    }

    @Override
    public void setElevation(float elevation) {
        super.setElevation(elevation);
        try {
            AppBarLayout parent = (AppBarLayout) getParent();
            if (elevation <= 0) {
                parent.setStateListAnimator(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        if (onDoubleTapListener != null) {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return onDoubleTapListener.onToolbarDoubleTaped();
                }
            });
            setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        }
    }

    public interface OnDoubleTapListener {
        boolean onToolbarDoubleTaped();
    }

}
