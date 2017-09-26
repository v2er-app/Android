package me.ghui.v2er.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import me.ghui.v2er.util.ScaleUtils;

/**
 * Created by ghui on 03/04/2017.
 */

public class V2erPtrFrameLayout extends PtrFrameLayout {

    private V2erHeaderView mPtrHeader;

    public V2erPtrFrameLayout(Context context) {
        super(context);
        init();
    }

    public V2erPtrFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public V2erPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPtrHeader = new V2erHeaderView(getContext());
        mPtrHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int padding = ScaleUtils.dp(8);
        mPtrHeader.setPadding(0, padding, 0, padding);
        setHeaderView(mPtrHeader);
        addPtrUIHandler(mPtrHeader);
    }

    @Override
    public void autoRefresh(final boolean atOnce, final int duration) {
        post(() -> V2erPtrFrameLayout.super.autoRefresh(atOnce, duration));
    }

    public void setContentView(View contentView) {
        if (mContent != null && contentView != null && mContent != contentView) {
            removeView(mContent);
        }
        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            contentView.setLayoutParams(lp);
        }
        mContent = contentView;
        addView(mContent, 0);
    }

    public PtrUIHandler getHeader() {
        return mPtrHeader;
    }

}
