package me.ghui.v2ex.module.home;

import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

    private NewsContract.IView mView;

    public NewsPresenter(NewsContract.IView view) {
        mView = view;
    }


    @Override
    public void start() {
        APIService.get()
                .homeNews("all")
                .compose(mView.<NewsInfo>rx())
                .subscribe(newsInfo -> mView.fillView(newsInfo, false));

    }

    @Override
    public void loadMore(int page) {
        APIService.get()
                .recentNews(page - 1)
                .compose(mView.<NewsInfo>rx())
                .subscribe(newsInfo -> mView.fillView(newsInfo, true));
    }


}

