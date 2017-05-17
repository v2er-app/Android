package me.ghui.v2ex.module.drawer.star;

import android.os.Bundle;

import me.ghui.v2ex.module.base.BaseFragment;

/**
 * Created by ghui on 17/05/2017.
 */

public class NodeStarFragment extends BaseFragment {

    public static NodeStarFragment newInstance() {

        Bundle args = new Bundle();

        NodeStarFragment fragment = new NodeStarFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int attachLayoutRes() {
        return 0;
    }

    @Override
    protected void startInject() {

    }

    @Override
    protected void init() {

    }
}
