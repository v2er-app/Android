package me.ghui.v2er.network.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SoV2EXSearchResultInfo extends BaseInfo{
    private int total;
    private List<Hit> hits;

    public int getTotal() {
        return total;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public static class Hit {
        @SerializedName("_source")
        private Source source;

        public static class Source {
            private String id;
            private String title;
            private String content;
            @SerializedName("node")
            private String nodeId;
            private long replies;
            private String time;
            @SerializedName("member")
            private String creator;

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }

            public String getNodeId() {
                return nodeId;
            }

            public long getReplies() {
                return replies;
            }

            public String getTime() {
                return time;
            }

            public String getCreator() {
                return creator;
            }

            @Override
            public String toString() {
                return "Source{" +
                        "id='" + id + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", nodeId='" + nodeId + '\'' +
                        ", replies=" + replies +
                        ", time='" + time + '\'' +
                        ", creator='" + creator + '\'' +
                        '}';
            }
        }

        public Source getSource() {
            return source;
        }

        @Override
        public String toString() {
            return "Hit{" +
                    "source=" + source +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SoV2EXSearchResultInfo{" +
                "total=" + total +
                ", hits=" + hits +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
