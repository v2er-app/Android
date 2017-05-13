package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 12/05/2017.
 * https://www.v2ex.com/my/following?p=1
 */

@Pick("div.content")
public class CareInfo {
    @Pick("div.fr.f12 strong.gray")
    private int total;
    @Pick("div.cell.item")
    private List<Item> items;

    public static class Item {
        @Pick("img.avatar")
        private String avatar;
        @Pick("span.item_title a[href^=/t/]")
        private String title;
        @Pick(value = "span.item_title a[href^=/t/]", attr = "href")
        private String link;
        @Pick("a.count_livid")
        private String comentNum;
    }
}
