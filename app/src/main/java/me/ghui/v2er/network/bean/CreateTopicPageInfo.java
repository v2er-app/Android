package me.ghui.v2er.network.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;

/**
 * Created by ghui on 05/06/2017.
 */

@Pick("div#Wrapper")
public class CreateTopicPageInfo extends BaseInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick("a.node")
    private List<HotNode> hotNodes;
    @Pick("select[id=nodes] option[value]")
    private List<Node> nodes;
    private List<BaseNode> allNodes;
    @Pick("div.problem")
    private Problem problem;

    public Problem getProblem() {
        return problem;
    }

    /**
     * reutrn all nodes include hot nodes
     *
     * @return
     */
    public List<BaseNode> getNodes() {
        if (PreConditions.isEmpty(allNodes)) {
            allNodes = new ArrayList<>();
        } else {
            allNodes.clear();
        }
        allNodes.addAll(hotNodes);
        allNodes.addAll(nodes);
        allNodes.add(new Node("沙盒 / sandbox"));
        return allNodes;
    }

    public Map<String, String> toPostMap(String title, String content, String nodeId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("node_name", nodeId);
        map.put("once", once);
        return map;
    }

    @Override
    public String toString() {
        return "CreateTopicPageInfo{" +
                "once='" + once + '\'' +
                ", nodes=" + nodes +
                ", hotNodes=" + hotNodes +
                '}';
    }

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(once) && PreConditions.notEmpty(nodes);
    }

    public interface BaseNode extends Parcelable {
        String getTitle();

        String getId();
    }

    public static class HotNode implements BaseNode {
        @Pick
        private String title;
        @Pick(attr = Attrs.HREF)
        private String idText;

        public HotNode() {

        }

        protected HotNode(Parcel in) {
            title = in.readString();
            idText = in.readString();
        }

        public static final Creator<HotNode> CREATOR = new Creator<HotNode>() {
            @Override
            public HotNode createFromParcel(Parcel in) {
                return new HotNode(in);
            }

            @Override
            public HotNode[] newArray(int size) {
                return new HotNode[size];
            }
        };

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getId() {
            // "javascript:chooseNode('macos')"
            try {
                return idText.substring(idText.indexOf("'") + 1, idText.lastIndexOf("'"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(idText);
        }

        @Override
        public String toString() {
            return "HotNode{" +
                    "title='" + title + '\'' +
                    ", idText='" + idText + '\'' +
                    '}';
        }
    }

    //浏览器 / browsers
    public static class Node implements BaseNode {
        @Pick("option")
        private String titleAndId;

        public Node() {
        }

        public Node(String titleAndId) {
            this.titleAndId = titleAndId;
        }

        protected Node(Parcel in) {
            titleAndId = in.readString();
        }

        public static final Creator<Node> CREATOR = new Creator<Node>() {
            @Override
            public Node createFromParcel(Parcel in) {
                return new Node(in);
            }

            @Override
            public Node[] newArray(int size) {
                return new Node[size];
            }
        };

        @Override
        public String getTitle() {
            try {
                return titleAndId.split("/")[0].trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String getId() {
            try {
                return titleAndId.split("/")[1].trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(titleAndId);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "titleAndId='" + titleAndId + '\'' +
                    '}';
        }
    }

    public static class Problem {
        @Pick(attr = Attrs.OWN_TEXT)
        private String title;
        @Pick("ul li")
        private List<String> tips;

        public boolean isEmpty() {
            return PreConditions.isEmpty(tips) && PreConditions.isEmpty(title);
        }

        public List<String> getTips() {
            return tips;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "Problem{" +
                    "title='" + title + '\'' +
                    ", tips=" + tips +
                    '}';
        }
    }
}
