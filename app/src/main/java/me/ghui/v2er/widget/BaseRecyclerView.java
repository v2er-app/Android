package me.ghui.v2er.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import me.ghui.fruit.Attrs;
import me.ghui.v2er.R;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 05/05/2017.
 */

public class BaseRecyclerView extends RecyclerView {


    public BaseRecyclerView(Context context) {
        super(context);
        init(null);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setClipToPadding(false);
        if (attrs != null) {
            TypedArray styledAttrs = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BaseRecyclerView, 0, 0);
            boolean shouldPaddingNav = styledAttrs.getBoolean(R.styleable.BaseRecyclerView_shouldPaddingNav, true);
            styledAttrs.recycle();
            if (shouldPaddingNav) {
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + Utils.getNavigationBarHeight() + ScaleUtils.dp(20));
            }
        }
    }

    public void addDivider() {
        setDivider(getResources().getColor(R.color.divider_color), ScaleUtils.px2Dp(getResources().getDimension(R.dimen.divider_size)));
    }

    public void setDivider(@ColorInt int dividerColor, float dividerSizeDp) {
        addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(dividerColor)
                        .size(Math.round(ScaleUtils.dp(dividerSizeDp)))
                        .build());
    }

}
