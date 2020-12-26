package me.ghui.v2er.module.home;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.BingSearchResultInfo;
import me.ghui.v2er.network.bean.SoV2EXSearchResultInfo;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchContract {

    public interface IView extends BaseContract.IView {
        String getQueryStr();

        void fillView(SoV2EXSearchResultInfo resultInfo, boolean isLoadMore);
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void search(String query, int page);
    }
}
