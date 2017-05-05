package me.ghui.v2ex.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import me.ghui.v2ex.R;

/**
 * Created by ghui on 05/05/2017.
 */

public class BaseRecyclerView extends RecyclerView {

    private DividerItemDecoration mDividerItemDecoration;

    public BaseRecyclerView(Context context) {
        super(context);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public void addDivider() {
        setDivider(R.drawable.recyclerview_divider);
    }

    public void setDivider(@DrawableRes int dividerRes) {
        setDivider(ContextCompat.getDrawable(getContext(), dividerRes));
    }

    public void setDivider(Drawable divider) {
        if (mDividerItemDecoration == null) {
            mDividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            addItemDecoration(mDividerItemDecoration);
        }

        if (divider != null) {
            mDividerItemDecoration.setDrawable(divider);
        } else {
            removeItemDecoration(mDividerItemDecoration);
            mDividerItemDecoration = null;
        }
    }
}
