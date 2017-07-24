package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 02/06/2017.
 * http://cn.bing.com/search?q=android+site%3av2ex.com&first=21
 */

@Pick("ol[id=b_results]")
public class BingSearchResultInfo extends BaseInfo {

    @Pick("li.b_algo")
    private List<Item> items;
    @Pick(value = "a.sb_pagN", attr = Attrs.HREF)
    private String next;

    public boolean hasMore() {
        return PreConditions.notEmpty(next);
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "BingSearchResultInfo{" +
                "hasNext=" + items +
                ",items=" + items +
                '}';
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(items) <= 0) return true;
        return PreConditions.notEmpty(items.get(0).link);
    }

    public static class Item {
        @Pick(value = "h2")
        private String title;
        @Pick(value = "div.b_caption p")
        private String content;
        @Pick(value = "h2 a[href*=v2ex]", attr = Attrs.HREF)
        private String link;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            content = content.replace("移动版", "");
            return content;
        }

        public String getLink() {
            return link;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }
}
