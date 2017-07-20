package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;

/**
 * Created by ghui on 20/07/2017.
 */

public class MentionedRecyclerView extends BaseRecyclerView implements MultiItemTypeAdapter.OnItemCountChangedListener {

    private int ITEM_HEIGHT;
    private final int MAX_SHOW_SIZE = 3;

    public MentionedRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public MentionedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MentionedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        ITEM_HEIGHT = (int) context.getResources().getDimension(R.dimen.mentioned_list_height);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        MultiItemTypeAdapter multiItemTypeAdapter = (MultiItemTypeAdapter) adapter;
        multiItemTypeAdapter.setOnItemCountChangedListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePaddingTop(getAdapter().getItemCount());
    }

    private void updatePaddingTop(int itemCount) {
        int showSize = Math.min(MAX_SHOW_SIZE, itemCount);
        int top = getMeasuredHeight() - ITEM_HEIGHT * showSize;
        Logger.d("onSizeChanged, top: " + top + ", showSize: " + showSize);
        setPadding(getPaddingLeft(), top, getPaddingRight(), getPaddingBottom());
    }

    @Override
    public void onCountChanged(int newCount) {
        updatePaddingTop(newCount);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return super.onInterceptTouchEvent(e);
        // TODO: 20/07/2017 事件透传 
    }
}
