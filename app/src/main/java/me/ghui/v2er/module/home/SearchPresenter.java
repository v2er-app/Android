package me.ghui.v2er.module.home;

import android.net.Uri;

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
    //    http://cn.bing.com/search?q=android+site%3av2ex.com&first=21
    private static final String BING_URL = "https://cn.bing.com/search";

    private SearchContract.IView mView;
    private Uri.Builder mUriBuilder;
    // 是否已经尝试过一次, 个别情况下第一次bing搜索会出现搜索结果为空的情况
    private boolean mTried = false;

    public SearchPresenter(SearchContract.IView view) {
        mView = view;
        mUriBuilder = new Uri.Builder();
        mUriBuilder.scheme("https")
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
        query += " site:v2ex.com/t";
        mUriBuilder.appendQueryParameter("q", query);
        mUriBuilder.appendQueryParameter("first", String.valueOf(first));
        L.d("bing Search: " + mUriBuilder.build().toString());
        APIService.get().bingSearch(mUriBuilder.build().toString())
                .compose(mView.rx(page))
                .subscribe(new GeneralConsumer<BingSearchResultInfo>() {
                    @Override
                    public void onConsume(BingSearchResultInfo bingSearchResultInfo) {
                        // TODO: 2019-10-04 bingSearchResultInfo is null stm
                        if (Check.isEmpty(bingSearchResultInfo.getItems()) && !mTried && page == 1) {
                            // try it once
                            mTried = true;
                            Voast.debug("BingSearch result is null, try again");
                            start();
                        } else {
                            mView.fillView(bingSearchResultInfo, page > 1);
                        }
                    }
                });

    }
}
