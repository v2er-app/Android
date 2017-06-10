package me.ghui.v2er.adapter.base;

import android.content.Context;
import android.view.View;

/**
 * Created by ghui on 30/04/2017.
 */

public abstract class ItemViewDelegateAdapter<T> extends ItemViewDelegate<T> {

    public ItemViewDelegateAdapter(Context context) {
        super(context);
    }

    public abstract View getItemView();

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

}
