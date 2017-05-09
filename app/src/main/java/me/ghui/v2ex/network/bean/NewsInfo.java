package me.ghui.v2ex.network.bean;


import java.util.List;

import me.ghui.fruit.annotations.Pick;
import me.ghui.v2ex.util.Utils;


/**
 * Created by ghui on 04/04/2017.
 */

@Pick("div.box")
public class NewsInfo {
    @Pick("div.cell.item")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "items=" + items +
                '}';
    }

    public static class Item {
        @Pick(value = "span.item_title > a")
        private String title;
        @Pick(value = "span.item_title > a", attr = "href")
        private String linkPath;
        @Pick(value = "td > a > img", attr = "src")
        private String avatar;
        @Pick(value = "td > a", attr = "href")
        private String avatarLink;
        @Pick(value = "span.small.fade > strong > a")
        private String user;
        @Pick(value = "span.small.fade:last-child", attr = "ownText")
        private String time;
        @Pick(value = "span.small.fade > a")
        private String tagName;
        @Pick(value = "span.small.fade > a", attr = "href")
        private String tagLink;
        @Pick(value = "a.count_livid")
        private int replies;

        public int getReplies() {
            return replies;
        }

        public void setReplies(int replies) {
            this.replies = replies;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLinkPath() {
            return linkPath;
        }

        public String getTopicId() {
            // /t/358992#reply458
            return linkPath.substring(3, linkPath.indexOf("#"));
        }

        public void setLinkPath(String linkPath) {
            this.linkPath = linkPath;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatarLink() {
            return avatarLink;
        }

        public void setAvatarLink(String avatarLink) {
            this.avatarLink = avatarLink;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTime() {
            if (!Utils.isEmpty(time)) {
                return time.split("•")[0];
            }
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getTagLink() {
            return tagLink;
        }

        public void setTagLink(String tagLink) {
            this.tagLink = tagLink;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", linkPath='" + linkPath + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", avatarLink='" + avatarLink + '\'' +
                    ", user='" + user + '\'' +
                    ", time='" + time + '\'' +
                    ", tagName='" + tagName + '\'' +
                    ", tagLink='" + tagLink + '\'' +
                    ", replies=" + replies +
                    '}';
        }

    }
}
