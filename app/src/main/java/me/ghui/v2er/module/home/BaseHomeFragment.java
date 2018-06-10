package me.ghui.v2er.module.home;

import android.view.View;

import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.module.base.BaseFragment;

public abstract class BaseHomeFragment<T extends BaseContract.IPresenter> extends BaseFragment<T> {

    @Override
    protected void onLoadingViewCreated(View loadingView) {
        int padding = (int) (getResources().getDimension(R.dimen.toolbar_small_height) + getResources().getDimension(R.dimen.tablayout_height));
        loadingView.setPadding(0, 0, 0, padding);
    }
}
