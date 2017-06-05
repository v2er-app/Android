package me.ghui.v2ex.network.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 05/06/2017.
 */

@Pick("div.content")
public class CreateTopicPageInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick("select[id=nodes]")
    private List<Node> nodes;
    @Pick("a.node")
    private List<HotNode> hotNodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public List<HotNode> getHotNodes() {
        return hotNodes;
    }

    public Map<String, String> toPostMap(String title, String content, String nodeId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("node_name", nodeId);
        map.put("once", once);
        return map;
    }


    private static class HotNode {
        @Pick
        private String title;
        @Pick(attr = Attrs.HREF)
        private String idText;

        public String getTitle() {
            return title;
        }

        public String getId() {
            // "javascript:chooseNode('macos')"
            try {
                return idText.substring(idText.indexOf("'") + 1, idText.lastIndexOf("'"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    //浏览器 / browsers
    private static class Node {
        @Pick("option")
        private String titleAndId;

        public String getTitle() {
            try {
                return titleAndId.split("/")[0].trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getId() {
            try {
                return titleAndId.split("/")[1].trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
