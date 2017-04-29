package me.ghui.v2ex.adapter.base;


/**
 * Created by zhy on 16/6/22.
 */
public abstract class ItemViewDelegate<T> {

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public void convert(ViewHolder holder, T t, int position) {

    }

}
