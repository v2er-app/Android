package me.ghui.v2er.module.home;

import java.util.ArrayList;
import java.util.List;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 22/08/2017.
 */

public class TabInfo {
    private final static String[] TAB_1_TITLES = {"全部", "技术", "创意", "好玩", "Apple", "酷工作", "交易", "城市", "问与答", "最热", "R2", "节点", "关注"};
    private final static String[] TAB_1_VALUES = {"all", "tech", "creative", "play", "apple", "jobs", "deals", "city", "qna", "hot", "r2", "nodes", "members"};
    private final static String LAST_SELECTED_TAB_KEY = Utils.KEY("last_select_tab");
    private static List<TabInfo> defaults = new ArrayList<>(TAB_1_TITLES.length);
    public String title;
    public String value;
    public boolean needLogin = false;
    public TabInfo(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public static List<TabInfo> getDefault() {
        if (!defaults.isEmpty()) return defaults;
        for (int i = 0; i < TAB_1_TITLES.length; i++) {
            TabInfo tabInfo = new TabInfo(TAB_1_TITLES[i], TAB_1_VALUES[i]);
            if ((tabInfo.value.equals("nodes") || tabInfo.value.equals("members"))) {
                tabInfo.needLogin = true;
            }
            defaults.add(tabInfo);
        }
        return defaults;
    }

    public static void saveSelectTab(TabInfo tabInfo) {
        Pref.save(LAST_SELECTED_TAB_KEY, tabInfo.value);
    }

    public static TabInfo getSelectTab() {
        String value = Pref.read(LAST_SELECTED_TAB_KEY);
        if (Check.isEmpty(value)) value = TAB_1_VALUES[0];
        List<TabInfo> defaults = getDefault();
        for (int i = 0; i < defaults.size(); i++) {
            if (defaults.get(i).value.equals(value)) return defaults.get(i);
        }
        return defaults.get(0);
    }

    public boolean isDefaultTab() {
        return TAB_1_VALUES[0].equals(value);
    }

    @Override
    public String toString() {
        return "TabInfo{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
