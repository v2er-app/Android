package me.ghui.v2ex.module.home;

import android.os.Bundle;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseFragment;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchFragment extends BaseFragment<SearchContract.IPresenter> implements SearchContract.IView {

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.frag_search_layout;
    }

    @Override
    protected void startInject() {

    }

    @Override
    protected void init() {

    }

}
