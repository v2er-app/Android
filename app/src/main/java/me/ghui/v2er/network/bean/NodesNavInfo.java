package me.ghui.v2er.network.bean;

import java.util.ArrayList;
import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 21/05/2017.
 * https://www.v2ex.com/
 * bottom box
 */

@Pick("div.box:last-child div > table")
public abstract class NodesNavInfo extends BaseInfo implements List<NodesNavInfo.Item> {

    public static class Item {
        @Pick("span.fade")
        private String category;
        @Pick("a")
        private List<NodeItem> nodes;

        @Override
        public String toString() {
            return "Item{" +
                    "category='" + category + '\'' +
                    ", nodes=" + nodes +
                    '}';
        }

        public String getCategory() {
            return category;
        }

        public List<NodeItem> getNodes() {
            return nodes;
        }

        public static class NodeItem {
            @Pick
            private String name;
            @Pick(attr = Attrs.HREF)
            private String link;

            @Override
            public String toString() {
                return "NodeItem{" +
                        "name='" + name + '\'' +
                        ", link='" + link + '\'' +
                        '}';
            }

            public String getName() {
                return name;
            }

            public String getLink() {
                return link;
            }
        }
    }

}
