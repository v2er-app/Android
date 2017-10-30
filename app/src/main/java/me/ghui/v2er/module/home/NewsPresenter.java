package me.ghui.v2er.module.home;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.NewsInfo;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

    private NewsContract.IView mView;
    //title index map for current items
    private HashMap<String, Integer> mCurrentItemsIndexMap;

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
                        Logger.d("newsInfo: " + newsInfo);
                        if (mCurrentItemsIndexMap != null) {
                            mCurrentItemsIndexMap.clear();
                            mCurrentItemsIndexMap = null;
                        }
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
                        mView.fillView(newsInfo, true);
                    }
                });
    }

    private void checkDuplicateItem(NewsInfo newsInfo) {
        if (newsInfo == null || Check.isEmpty(newsInfo.getItems())) return;
        if (mCurrentItemsIndexMap == null) {
            List<NewsInfo.Item> currentItems = mView.getNewsInfo();
            mCurrentItemsIndexMap = new HashMap<>();
            for (int i = 0; i < currentItems.size(); i++) {
                NewsInfo.Item item = currentItems.get(i);
                mCurrentItemsIndexMap.put(item.getTitle(), i);
            }
        }

        //check
        List<NewsInfo.Item> newItems = newsInfo.getItems();
        for (int i = 0; i < newItems.size(); i++) {
            NewsInfo.Item item = newItems.get(i);
            Integer index = mCurrentItemsIndexMap.get(item.getTitle());
            if (index == null) {
                mCurrentItemsIndexMap.put(item.getTitle(), mCurrentItemsIndexMap.size());
            } else {
                Logger.e("duplicate items: " + item.getTitle());
                newItems.remove(item);
            }
        }

    }


}

