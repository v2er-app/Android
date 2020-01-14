package me.ghui.v2er.network.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.util.Check;

/**
 * Created by ghui on 21/05/2017.
 * https://www.v2ex.com/
 * bottom box
 */

@Pick("div.box:last-child div > table")
public class NodesNavInfo extends ArrayList<NodesNavInfo.Item> implements IBase {
    private String mResponseBody;

    @Override
    public String getResponse() {
        return mResponseBody;
    }

    @Override
    public void setResponse(String response) {
        mResponseBody = response;
    }

    @Override
    public boolean isValid() {
        if (size() <= 0) return true;
        return Check.notEmpty(get(0).category);
    }

    public static class Item implements Serializable {
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

        public static class NodeItem implements Serializable{
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
