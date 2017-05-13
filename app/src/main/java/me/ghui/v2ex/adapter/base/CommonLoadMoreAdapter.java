package me.ghui.v2ex.adapter.base;

import android.content.Context;
import android.support.annotation.LayoutRes;

import javax.annotation.Nullable;

import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 10/05/2017.
 * Single Type Item LoadMore Adapter
 */

public abstract class CommonLoadMoreAdapter<T> extends LoadMoreRecyclerView.Adapter<T> {

    public CommonLoadMoreAdapter(Context context, @LayoutRes int layoutRes) {
        super(context);
        addItemViewDelegate(new ItemViewDelegate<T>(context) {
            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public boolean isForViewType(@Nullable T item, int position) {
                return !isLoadMoreFooterItem(position);
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                CommonLoadMoreAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ViewHolder holder, T t, int position);
}
