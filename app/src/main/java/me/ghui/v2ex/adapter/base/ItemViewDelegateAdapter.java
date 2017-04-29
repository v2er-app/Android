package me.ghui.v2ex.adapter.base;

import android.view.View;

/**
 * Created by ghui on 30/04/2017.
 */

public abstract class ItemViewDelegateAdapter<T> extends ItemViewDelegate<T> {

    public abstract View getItemView();

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

}
