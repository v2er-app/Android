package me.ghui.v2er.network.bean;

import java.io.Serializable;

public class ExplorePageInfoWrapper extends BaseInfo implements Serializable {

    public ExplorePageInfo explorePageInfo;


    private ExplorePageInfoWrapper(ExplorePageInfo explorePageInfo) {
        this.explorePageInfo = explorePageInfo;
    }

    public static ExplorePageInfoWrapper wrapper(ExplorePageInfo explorePageInfo) {
        return new ExplorePageInfoWrapper(explorePageInfo);
    }

    @Override
    public boolean isValid() {
        if (explorePageInfo == null) return false;
        return explorePageInfo.isValid();
    }
}
