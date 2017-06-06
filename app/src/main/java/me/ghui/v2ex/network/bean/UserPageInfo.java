package me.ghui.v2ex.network.bean;

import java.util.ArrayList;
import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2ex.network.Constants;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 01/06/2017.
 * https://www.v2ex.com/member/ghui
 */

@Pick("div.content")
public class UserPageInfo {
    @Pick("h1")
    private String userName;
    @Pick(value = "img.avatar", attr = Attrs.SRC)
    private String avatar;
    @Pick("td[valign=top] > span.gray")
    private String desc;
    @Pick("strong.online")
    private String online;
    @Pick(value = "div.fr input", attr = "value")
    private String unfollow;
    @Pick("div.box:has(div.cell_tabs) > div.cell.item")
    private List<TopicItem> topicItems;
    @Pick("div.box:last-child > div.dock_area")
    private List<ReplyDockItem> dockItems;
    @Pick("div.box:last-child div.reply_content")
    private List<ReplyContentItem> replyContentItems;

    private List<Item> items;
    private List<ReplyItem> replyItems;

    private List<ReplyItem> getReplyItems() {
        if (Utils.isEmpty(dockItems)) return null;
        if (replyItems == null) {
            replyItems = new ArrayList<>(dockItems.size());
        } else {
            replyItems.clear();
        }
        for (int i = 0; i < dockItems.size(); i++) {
            replyItems.add(new ReplyItem(dockItems.get(i), replyContentItems.get(i)));
        }
        return replyItems;
    }

    public List<Item> getItems() {
        if (items == null) {
            items = new ArrayList<>(dockItems.size() + replyContentItems.size());
        } else {
            items.clear();
        }

        items.addAll(topicItems);
        items.addAll(getReplyItems());
        return items;
    }

    public boolean isOnline() {
        return Utils.isnodempty(online) && online.equals("ONLINE");
    }

    public boolean isFollowed() {
        return Utils.isnodempty(unfollow) && unfollow.contains("取消");
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return Constants.HTTPS_SCHEME + avatar;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "UserPageInfo{" +
                "userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", desc='" + desc + '\'' +
                ", online='" + online + '\'' +
                ", topicItems=" + topicItems +
                ", replyItems=" + replyItems +
                '}';
    }

    public abstract static class Item {
        public abstract String getTopicLink();
    }

    public static class TopicItem extends Item {
        @Pick("strong > a[href^=/member/]:first-child")
        private String userName;
        @Pick("a.node")
        private String tag;
        @Pick(value = "a.node", attr = Attrs.HREF)
        private String tagLink;
        @Pick("span.item_title")
        private String title;
        @Pick(value = "span.item_title a", attr = Attrs.HREF)
        private String link;
        @Pick("span.small.fade:last-child")
        private String time;
        @Pick("a.count_livid")
        private int replyNum;

        @Override
        public String toString() {
            return "TopicItem{" +
                    "userName='" + userName + '\'' +
                    ", tag='" + tag + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", time='" + time + '\'' +
                    ", replyNum=" + replyNum +
                    '}';
        }

        public String getUserName() {
            return userName;
        }

        public String getTag() {
            return tag;
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getTime() {
            return time;
        }

        public int getReplyNum() {
            return replyNum;
        }

        @Override
        public String getTopicLink() {
            return link;
        }
    }

    public static class ReplyItem extends Item {
        private ReplyDockItem dockItem;
        private ReplyContentItem contentItem;

        public ReplyItem(ReplyDockItem dockItem, ReplyContentItem contentItem) {
            this.dockItem = dockItem;
            this.contentItem = contentItem;
        }

        @Override
        public String toString() {
            return "ReplyItem{" +
                    "dockItem=" + dockItem +
                    ", contentItem=" + contentItem +
                    '}';
        }

        public String getTitle() {
            return dockItem.getTitle();
        }

        public String getLink() {
            return dockItem.getLink();
        }

        public String getTime() {
            return dockItem.getTime();
        }

        public String getContent() {
            return contentItem.getContent();
        }

        @Override
        public String getTopicLink() {
            return dockItem.getLink();
        }
    }

    private static class ReplyDockItem {
        @Pick("span.gray")
        private String title;
        @Pick(value = "span.gray > a", attr = Attrs.HREF)
        private String link;
        @Pick("span.fade")
        private String time;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return "ReplyDockItem{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    private static class ReplyContentItem {
        @Pick
        private String content;

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "ReplyContentItem{" +
                    "content='" + content + '\'' +
                    '}';
        }
    }
}
