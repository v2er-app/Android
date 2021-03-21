package me.ghui.v2er.adapter.base;


import android.content.Context;
import androidx.annotation.Nullable;


/**
 * Created by zhy on 16/6/22.
 */
public abstract class ItemViewDelegate<T> {
    protected Context mContext;

    public ItemViewDelegate(Context context) {
        mContext = context;
    }

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(@Nullable T item, int position);

    public void convert(ViewHolder holder, T t, int position) {

    }

}
