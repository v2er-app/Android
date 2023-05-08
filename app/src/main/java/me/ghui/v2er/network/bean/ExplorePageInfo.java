package me.ghui.v2er.network.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExplorePageInfo extends BaseInfo {

    private final List<Serializable> items = new ArrayList<>();

    public List<Serializable> getItems() {
        if (items.size() > 0) {
            items.clear();
        }
        if (dailyHotInfo != null) {
            items.add(dailyHotInfoTitle);
            items.addAll(dailyHotInfo);
        }
        if (nodesNavInfo != null) {
            items.add(nodesNavInfoTitle);
            items.addAll(nodesNavInfo);
        }
        return items;
    }

    public DailyHotInfo dailyHotInfo;
    public String dailyHotInfoTitle;

    public void setDailyHotInfo(DailyHotInfo dailyHotInfo, String title) {
        this.dailyHotInfo = dailyHotInfo;
        this.dailyHotInfoTitle = title;
    }

    public void setDailyHotInfoTitle(String dailyHotInfoTitle) {
        this.dailyHotInfoTitle = dailyHotInfoTitle;
    }

    public NodesNavInfo nodesNavInfo;
    private String nodesNavInfoTitle;

    public void setNodesNavInfo(NodesNavInfo nodesNavInfo, String title) {
        this.nodesNavInfo = nodesNavInfo;
        this.nodesNavInfoTitle = title;
    }

    public void setNodesNavInfoTitle(String nodesNavInfoTitle) {
        this.nodesNavInfoTitle = nodesNavInfoTitle;
    }

    @Override
    public boolean isValid() {
        return this.dailyHotInfo.isValid() || this.nodesNavInfo.isValid();
    }


}
