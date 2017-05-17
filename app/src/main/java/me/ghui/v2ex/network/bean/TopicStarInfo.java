package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 17/05/2017.
 * https://www.v2ex.com/my/topics
 */

@Pick("div.content")
public class TopicStarInfo {
    @Pick("div.fr.f12 strong.gray")
    private int total = 0;
    @Pick("div.cell.item")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "TopicStarInfo{" +
                "total=" + total +
                ", items=" + items +
                '}';
    }

    public static class Item {
        @Pick("td>a[href^=/member]")
        private String userLink;
        @Pick(value = "img.avatar", attr = Attrs.SRC)
        private String avatar;
        @Pick("span.item_title")
        private String title;
        @Pick(value = "span.item_title a", attr = Attrs.HREF)
        private String link;
        @Pick("a.count_livid")
        private int commentNum;
        @Pick("a.node")
        private String tag;
        @Pick(value = "a.node", attr = Attrs.HREF)
        private String tagLink;
        @Pick(value = "span.small.fade", attr = Attrs.OWN_TEXT)
        private String time;

        @Override
        public String toString() {
            return "Item{" +
                    "userLink='" + userLink + '\'' +
                    "userName='" + getUserName() + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", commentNum=" + commentNum +
                    ", tag='" + tag + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    '}';
        }

        public String getTopicId() {
            // /t/358992#reply458
            return link.substring(3, link.indexOf("#"));
        }

        public String getTime() {
            //  •  36 天前  •  最后回复来自
            if (!Utils.isEmpty(time)) {
                return time.trim().split("•")[1].trim();
            }
            return time;
        }

        public String getUserName() {
            if (Utils.isEmpty(userLink)) return null;
            else {
                String[] ss = userLink.split("/");
                return ss[ss.length - 1];
            }
        }

        public String getUserLink() {
            return userLink;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public String getTag() {
            return tag;
        }

        public String getTagLink() {
            return tagLink;
        }
    }
}
