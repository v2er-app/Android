package me.ghui.v2er.module.home;

import android.net.Uri;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.BingSearchResultInfo;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchPresenter implements SearchContract.IPresenter {
    //    http://cn.bing.com/search?q=android+site%3av2ex.com&first=21
    private static final String BING_URL = "http://cn.bing.com/search";

    private SearchContract.IView mView;
    private Uri.Builder mUriBuilder;

    public SearchPresenter(SearchContract.IView view) {
        mView = view;
        mUriBuilder = new Uri.Builder();
        mUriBuilder.scheme("http")
                .authority("cn.bing.com")
                .appendPath("search");
    }

    @Override
    public void start() {
        search(mView.getQueryStr(), 1);
    }

    @Override
    public void search(String query, int page) {
        int first = (page - 1) * 10 + 1;
        mUriBuilder.clearQuery();
        query += " site:v2ex.com";
        mUriBuilder.appendQueryParameter("q", query);
        mUriBuilder.appendQueryParameter("first", String.valueOf(first));
        Logger.d("bing Search: " + mUriBuilder.build().toString());
        APIService.get().bingSearch(mUriBuilder.build().toString())
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<BingSearchResultInfo>() {
                    @Override
                    public void onConsume(BingSearchResultInfo bingSearchResultInfo) {
                        mView.fillView(bingSearchResultInfo, page > 1);
                    }
                });

    }
}
