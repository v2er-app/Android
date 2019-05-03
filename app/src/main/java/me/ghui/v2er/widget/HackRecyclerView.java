package me.ghui.v2er.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ghui on 15/11/2017.
 * https://stackoverflow.com/questions/45192654/how-to-avoid-collapsingtoolbarlayout-not-being-snapped-or-being-wobbly-when-sc
 */

public class HackRecyclerView extends LoadMoreRecyclerView {

    @SuppressWarnings("unused")
    private static final String TAG = "MyRecyclerView";
    private AppBarTracking mAppBarTracking;
    private View mView;
    private int mTopPos;
    private LinearLayoutManager mLayoutManager;
    public HackRecyclerView(Context context) {
        this(context, null);
    }
    public HackRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HackRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {

        // App bar latching trouble is only with this type of movement when app bar is expanded
        // or collapsed. In touch mode, everything is OK regardless of the open/closed status
        // of the app bar.
        if (type == ViewCompat.TYPE_NON_TOUCH && mAppBarTracking != null && mAppBarTracking.isAppBarIdle()
                && isNestedScrollingEnabled()) {
            // Make sure the AppBar stays expanded when it should.
            if (dy > 0) { // swiped up
                if (mAppBarTracking.isAppBarExpanded()) {
                    // Appbar can only leave its expanded state under the power of touch...
                    consumed[1] = dy;
                    return true;
                }
            } else { // swiped down (or no change)
                // Make sure the AppBar stays collapsed when it should.
                // Only dy < 0 will open the AppBar. Stop it from opening by consuming dy if needed.
                mTopPos = mLayoutManager.findFirstVisibleItemPosition();
                if (mTopPos == 0) {
                    mView = mLayoutManager.findViewByPosition(mTopPos);
                    if (-mView.getTop() + dy <= 0) {
                        // Scroll until scroll position = 0 and AppBar is still collapsed.
                        consumed[1] = dy - mView.getTop();
                        return true;
                    }
                }
            }
        }

        boolean returnValue = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        // Fix the scrolling problems when scrolling is disabled. This issue existed prior
        // to 26.0.0-beta2.
        if (offsetInWindow != null && !isNestedScrollingEnabled() && offsetInWindow[1] != 0) {
            offsetInWindow[1] = 0;
        }
        return returnValue;
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        super.setLayoutManager(layout);
        mLayoutManager = (LinearLayoutManager) getLayoutManager();
    }

    public void setAppBarTracking(AppBarTracking appBarTracking) {
        mAppBarTracking = appBarTracking;
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            return false;
        }
        return super.hasNestedScrollingParent(type);
    }

    public interface AppBarTracking {
        boolean isAppBarIdle();

        boolean isAppBarExpanded();
    }
}
