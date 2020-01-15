package me.ghui.v2er.module.home;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.util.L;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

    private NewsContract.IView mView;
    //title index map for current items
    private Set<String> idSet;
    private int mPage = 1;

    @Override
    public int getPage() {
        return mPage;
    }

    public NewsPresenter(NewsContract.IView view) {
        mView = view;
    }


    @Override
    public void start() {
        APIService.get()
                .homeNews(mView.getCurrentTab().value)
                .compose(mView.<NewsInfo>rx())
                .subscribe(new GeneralConsumer<NewsInfo>(mView) {
                    @Override
                    public void onConsume(NewsInfo newsInfo) {
                        idSet = null;
                        mView.fillView(newsInfo, false);
                    }
                });

    }

    @Override
    public void loadMore(int page) {
        APIService.get()
                .recentNews(page - 1)
                .compose(mView.<NewsInfo>rx(page))
                .map(newsInfo -> {
                    checkDuplicateItem(newsInfo);
                    return newsInfo;
                })
                .subscribe(new GeneralConsumer<NewsInfo>(mView) {
                    @Override
                    public void onConsume(NewsInfo newsInfo) {
                        mPage = page;
                        mView.fillView(newsInfo, true);
                    }
                });
    }


    private void checkDuplicateItem(NewsInfo newsInfo) {
        if (newsInfo == null || Check.isEmpty(newsInfo.getItems())) return;
        if (idSet == null) {
            idSet = new HashSet<>();
            List<NewsInfo.Item> currentItems = mView.getNewsInfo();
            for (int i = 0; i < currentItems.size(); i++) {
                idSet.add(currentItems.get(i).getId());
            }
        }

        //check
        List<NewsInfo.Item> newItems = newsInfo.getItems();
        for (int i = 0; i < newItems.size(); i++) {
            NewsInfo.Item item = newItems.get(i);
            if (idSet.contains(item.getId())) {
                L.e("duplicate item: " + item.getId());
                newItems.remove(item);
            } else {
                idSet.add(item.getId());
            }
        }

    }


}

