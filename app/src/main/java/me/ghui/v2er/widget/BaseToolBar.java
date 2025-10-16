package me.ghui.v2er.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;

/**
 * Created by ghui on 21/03/2017.
 */

public class BaseToolBar extends Toolbar {
    private View mNavigationBadge;
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
        getNavigationIcon().setTint(Theme.getColor(R.attr.icon_tint_color, getContext()));
        setNavigationContentDescription(R.string.acc_back);
    }

    /**
     * Initialize navigation badge for toolbar
     * Call this when you need badge support on the navigation icon
     */
    public void initNavigationBadge() {
        if (mNavigationBadge != null) {
            return; // Already initialized
        }

        // Create badge view
        mNavigationBadge = new View(getContext());
        int badgeSize = ScaleUtils.dp(8);
        LayoutParams badgeParams = new LayoutParams(badgeSize, badgeSize);
        badgeParams.gravity = Gravity.TOP | Gravity.START;

        // Position badge at top-right corner of navigation icon
        // Navigation icon is typically 24dp with 16dp padding from left
        // Badge should be at: left padding (16dp) + icon size (24dp) - overlap (4dp)
        badgeParams.leftMargin = ScaleUtils.dp(36);
        badgeParams.topMargin = ScaleUtils.dp(12); // Centered vertically minus offset

        mNavigationBadge.setLayoutParams(badgeParams);
        mNavigationBadge.setBackgroundResource(R.drawable.shape_badge_dot);
        mNavigationBadge.setVisibility(GONE);

        // Add badge to toolbar
        addView(mNavigationBadge);
    }

    /**
     * Show the navigation badge (red dot)
     */
    public void showNavigationBadge() {
        // Initialize badge if needed
        if (mNavigationBadge == null) {
            initNavigationBadge();
        }

        if (mNavigationBadge != null) {
            mNavigationBadge.setVisibility(VISIBLE);
        }
    }

    /**
     * Hide the navigation badge
     */
    public void hideNavigationBadge() {
        if (mNavigationBadge != null) {
            mNavigationBadge.setVisibility(GONE);
        }
    }

    /**
     * Check if navigation badge is showing
     */
    public boolean isNavigationBadgeShowing() {
        return mNavigationBadge != null && mNavigationBadge.getVisibility() == VISIBLE;
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
        int tintColor = Theme.getColor(R.attr.icon_tint_color, getContext());
        Menu menu = getMenu();
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.setTint(tintColor);
            }
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
