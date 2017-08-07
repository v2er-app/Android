package me.ghui.v2er.module.home;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NewsInfo;

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
        boolean useR2 = Pref.readBool(R.string.pref_key_r2);
        APIService.get()
                .homeNews(useR2 ? "r2" : "all")
                .compose(mView.<NewsInfo>rx())
                .subscribe(new GeneralConsumer<NewsInfo>() {
                    @Override
                    public void onConsume(NewsInfo newsInfo) {
                        Logger.d("newsInfo: " + newsInfo);
                        mView.fillView(newsInfo, false);
                    }
                });

    }

    @Override
    public void loadMore(int page) {
        APIService.get()
                .recentNews(page - 1)
                .compose(mView.<NewsInfo>rx(page))
                .subscribe(new GeneralConsumer<NewsInfo>() {
                    @Override
                    public void onConsume(NewsInfo newsInfo) {
                        mView.fillView(newsInfo, true);
                    }
                });
    }


}

