package me.ghui.v2er.widget.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ghui on 03/06/2017.
 */

public class NewsNewFabBehavior extends FloatingActionButton.Behavior {

    public NewsNewFabBehavior() {
    }

    public NewsNewFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

}
