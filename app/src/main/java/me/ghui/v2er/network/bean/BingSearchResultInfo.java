package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 02/06/2017.
 * http://cn.bing.com/search?q=android+site%3av2ex.com&first=21
 */

@Pick("ol[id=b_results]")
public class BingSearchResultInfo extends BaseInfo {

    @Pick("li.b_algo")
    private List<Item> items;
    @Pick(value = "a.sb_fullnpl")
    private String next;
    @Pick(value = "a.sb_halfnext")
    private String next2;

    public boolean hasMore() {
        return Check.notEmpty(next) || Check.notEmpty(next2);
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "BingSearchResultInfo{" +
                "hasNext=" + hasMore() +
                ",items=" + items +
                '}';
    }

    @Override
    public boolean isValid() {
        if (Utils.listSize(items) <= 0) return true;
        return Check.notEmpty(items.get(0).link);
    }

    public static class Item {
        @Pick(value = "h2")
        private String title;
        @Pick(value = "div.b_caption p")
        private String content;
        @Pick(value = "div.b_algoheader a", attr = Attrs.HREF)
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
