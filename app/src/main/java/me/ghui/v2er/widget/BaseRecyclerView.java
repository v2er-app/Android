package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import me.ghui.v2er.R;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 05/05/2017.
 */

public class BaseRecyclerView extends RecyclerView {


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
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + Utils.getNavigationBarHeight());
        setClipToPadding(false);
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
