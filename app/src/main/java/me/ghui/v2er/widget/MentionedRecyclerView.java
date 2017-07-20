package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;

/**
 * Created by ghui on 20/07/2017.
 */

public class MentionedRecyclerView extends BaseRecyclerView {

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int showSize;
        int itemCount = getAdapter().getItemCount();
        if (itemCount >= MAX_SHOW_SIZE) {
            showSize = MAX_SHOW_SIZE;
        } else {
            showSize = itemCount;
        }
        int top = h - ITEM_HEIGHT * showSize;
        Logger.d("onSizeChanged, top: " + top + ", showSize: " + showSize);
        setPadding(getPaddingLeft(), top, getPaddingRight(), getPaddingBottom());
    }
}
