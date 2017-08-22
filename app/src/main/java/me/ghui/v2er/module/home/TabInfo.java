package me.ghui.v2er.module.home;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghui on 22/08/2017.
 */

public class TabInfo {
    private final static String[] TAB_1_TITLES = {"技术", "创意", "好玩", "Apple", "酷工作", "交易", "城市", "问与答", "最热", "全部", "R2", "节点", "关注"};
    private final static String[] TAB_1_VALUES = {"tech", "creative", "play", "apple", "jobs", "deals", "city", "qna", "hot", "all", "r2", "nodes", "members"};
    private static List<TabInfo> defaults = new ArrayList<>(TAB_1_TITLES.length);

    public TabInfo(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String title;
    public String value;

    @Override
    public String toString() {
        return "TabInfo{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static List<TabInfo> getDefault() {
        if (!defaults.isEmpty()) return defaults;
        for (int i = 0; i < TAB_1_TITLES.length; i++) {
            defaults.add(new TabInfo(TAB_1_TITLES[i], TAB_1_VALUES[i]));
        }
        return defaults;
    }

}
