package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 02/06/2017.
 * http://cn.bing.com/search?q=android+site%3av2ex.com&first=21
 */

@Pick("ol[id=b_results]")
public class BingSearchResultInfo {

    @Pick("li.b_algo")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "BingSearchResultInfo{" +
                "items=" + items +
                '}';
    }

    public static class Item {
        @Pick(value = "h2")
        private String title;
        @Pick(value = "div.b_caption")
        private String content;
        @Pick(value = "h2 a[href*=v2ex]", attr = Attrs.HREF)
        private String link;

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

//        public String getLink() {
//            return link;
//        }

        // TODO: 02/06/2017  

        public int getTopicId() {
            try {
                String[] str = link.split("/");
                return Integer.parseInt(str[str.length - 1]);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
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
