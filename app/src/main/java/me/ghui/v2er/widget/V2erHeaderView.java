package me.ghui.v2er.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by ghui on 24/09/2017.
 */

public class V2erHeaderView extends View implements PtrUIHandler {

    public V2erHeaderView(Context context) {
        super(context);
    }

    public V2erHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public V2erHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V2erHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {

    }


    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        // TODO: 24/09/2017 start rotation
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        // arrow up
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
