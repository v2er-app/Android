package me.ghui.v2er.module.home;

import android.view.View;

import java.io.Serializable;

import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.network.bean.BaseInfo;

import static me.ghui.v2er.util.Utils.KEY;

public abstract class BaseHomeFragment<T extends BaseContract.IPresenter> extends BaseFragment<T> {
    protected static final String KEY_DATA = KEY("page_data");

    @Override
    protected void onLoadingViewCreated(View loadingView) {
        int padding = (int) (getResources().getDimension(R.dimen.toolbar_small_height) + getResources().getDimension(R.dimen.tablayout_height));
        loadingView.setPadding(0, 0, 0, padding);
    }

    public static class RestoreData<T extends BaseInfo> implements Serializable {
        public int page;
        public int scrollPos;
        public int scrollOffset;
        public T info;

        public RestoreData(int page, int scrollPos, int scrollOffset, T t) {
            this.page = page;
            this.scrollPos = scrollPos;
            this.scrollOffset = scrollOffset;
            info = t;
        }
    }

}
