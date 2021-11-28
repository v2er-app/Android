package me.ghui.v2er.module.home;

import android.net.Uri;

import me.ghui.v2er.network.bean.SoV2EXSearchResultInfo;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.BingSearchResultInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchPresenter implements SearchContract.IPresenter {
    private SearchContract.IView mView;

    public SearchPresenter(SearchContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        search(mView.getQueryStr(), 1);
    }

    @Override
    public void search(String query, int page) {
        int from = (page - 1) * 10;

//        String sortWay = "sumup";
        String sortWay = "created";
        APIService.get().search(query, from, sortWay)
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<SoV2EXSearchResultInfo>() {
                    @Override
                    public void onConsume(SoV2EXSearchResultInfo soV2EXSearchResultInfo) {
                        mView.fillView(soV2EXSearchResultInfo, page > 1);
                    }
                });
    }
}
