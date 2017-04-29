package me.ghui.v2ex.adapter.base;

import android.content.Context;
import android.view.View;

import java.util.List;

import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 25/04/2017.
 */

public class LoadMoreAdapter<T> extends MultiItemTypeAdapter<T> {

    public LoadMoreAdapter(final Context context) {
        super(context, null);

        addItemViewDelegate(new ItemViewDelegateAdapter<T>() {

            @Override
            public View getItemView() {
                return new LoadMoreRecyclerView.CommonLoadMoreFooter(context);
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return position == getItemCount() - 1;
            }

        });
    }

    public void setData(List<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

}
