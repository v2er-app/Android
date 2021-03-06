package me.ghui.v2er.module.home;

import java.util.List;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.NewsInfo;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsContract {

    public interface IView extends BaseContract.IView {
        void fillView(NewsInfo newsInfos, boolean isLoadMore);

        List<NewsInfo.Item> getNewsInfo();

        TabInfo getCurrentTab();
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void loadMore(int page);
        int getPage();
    }

}
