package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2ex.network.Constants;

/**
 * Created by ghui on 18/05/2017.
 * https://www.v2ex.com/my/nodes
 */

@Pick("div#MyNodes")
public class NodeStarInfo {

    @Pick("a.grid_item")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @Pick(value = "img", attr = Attrs.SRC)
        private String img;
        @Pick(value = "div", attr = Attrs.OWN_TEXT)
        private String name;
        @Pick(value = "span.fade.f12")
        private int topicNum;
        @Pick(attr = Attrs.HREF)
        private String link;

        @Override
        public String toString() {
            return "Item{" +
                    "img='" + img + '\'' +
                    ", name='" + name + '\'' +
                    ", topicNum=" + topicNum +
                    ", link='" + link + '\'' +
                    ", id='" + getLink() + '\'' +
                    '}';
        }

        public String getImg() {
            return Constants.HTTPS_SCHEME + img;
        }

        public String getName() {
            return name;
        }

        public int getTopicNum() {
            return topicNum;
        }

        public String getLink() {
            return link;
        }
    }
}
