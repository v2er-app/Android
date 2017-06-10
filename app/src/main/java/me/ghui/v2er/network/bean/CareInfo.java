package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 12/05/2017.
 * https://www.v2ex.com/my/following?p=1
 */

@Pick("div.content")
public class CareInfo {
    @Pick("div.fr.f12 strong.gray")
    private int total;
    @Pick("div.cell.item")
    private List<Item> items;

    @Override
    public String toString() {
        return "CareInfo{" +
                "total=" + total +
                ", items=" + items +
                '}';
    }

    public int getTotal() {
        return total;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @Pick(value = "img.avatar", attr = Attrs.SRC)
        private String avatar;
        @Pick("strong a[href^=/member/]")
        private String userName;
        @Pick(value = "span.small.fade", attr = Attrs.OWN_TEXT)
        private String time;
        @Pick("span.item_title a[href^=/t/]")
        private String title;
        @Pick(value = "span.item_title a[href^=/t/]", attr = Attrs.HREF)
        private String link;
        @Pick("a.count_livid")
        private String comentNum;
        @Pick("a.node")
        private String tagName;
        @Pick(value = "a.node", attr = Attrs.HREF)
        private String tagLink;

        @Override
        public String toString() {
            return "Item{" +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + getTime() + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", comentNum='" + comentNum + '\'' +
                    ", tagName='" + tagName + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    '}';
        }

        public String getTime() {
            //  •  36 天前  •  最后回复来自
            if (!Utils.isEmpty(time)) {
                return time.trim().split("•")[1].trim();
            }
            return time;
        }

        public String getAvatar() {
            return Constants.HTTPS_SCHEME + avatar;
        }

        public String getUserName() {
            return userName;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getComentNum() {
            return comentNum;
        }

        public String getTagName() {
            return tagName;
        }

        public String getTagLink() {
            return tagLink;
        }
    }
}
