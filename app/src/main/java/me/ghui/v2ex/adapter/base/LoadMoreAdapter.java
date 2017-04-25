package me.ghui.v2ex.adapter.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;

import java.util.List;

import me.ghui.v2ex.R;

/**
 * Created by ghui on 25/04/2017.
 */

public class LoadMoreAdapter<T> extends MultiItemTypeAdapter<T> {

    protected Context mContext;
    protected int mLayoutId;
    protected LayoutInflater mInflater;

    public LoadMoreAdapter(Context context, @LayoutRes int layoutId) {
        super(context, null);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;

        addItemViewDelegate(new ItemViewDelegate<T>() {

            @Override
            public int getItemViewLayoutId() {
                return R.layout.common_loadmore_footer;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return position == getItemCount() - 1;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
            }
        });
    }

    public void setData(List<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

}
