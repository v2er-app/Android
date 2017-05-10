package me.ghui.v2ex.network.bean;

import java.util.ArrayList;
import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2ex.util.Utils;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo {

    @Pick("div.header")
    private HeaderInfo headerInfo;
    @Pick("div[id^=r_]")
    private List<Reply> replies;

    private List<Item> items;

    public List<Item> getItems() {
        if (items == null) {
            items = new ArrayList<>(Utils.listSize(replies) + 1);
        } else {
            items.clear();
        }
        items.add(headerInfo);
        items.addAll(replies);
        return items;
    }

    public int getTotalPage() {
        return headerInfo.getPage();
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "headerInfo=" + headerInfo +
                ", replies=" + replies +
                '}';
    }

    public static class HeaderInfo implements Item {
        @Pick(value = "div.header img.avatar", attr = "src")
        private String avatar;
        @Pick("small.gray a")
        private String userName;
        @Pick(value = "small.gray", attr = "ownText")
        private String time;
        @Pick("div.header a[href^=/go]")
        private String tag;
        @Pick("div.cell span.gray")
        private String comment;
        @Pick("a.page_normal:last-child")
        private int page;
        @Pick("h1")
        private String title;
        @Pick(value = "div.cell div.topic_content", attr = Attrs.HTML)
        private String contentHtml;
        @Pick("div.subtle")
        private List<PostScript> postScripts;

        public String getCommentNum() {
            if (Utils.isEmpty(comment)) return "评论0";
            return "评论" + comment.split(" ")[0];
        }

        public String getTag() {
            return tag;
        }

        public String getTime() {
            return time.split(",")[0].trim().substring(6).replaceAll(" ", "");
        }

        public int getViewCount() {
            String count = time.split(",")[1].trim();
            return Integer.parseInt(count.substring(0, count.indexOf(" ")));
        }

        public List<PostScript> getPostScripts() {
            return postScripts;
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getTitle() {
            return title;
        }

        public int getPage() {
            return page < 1 ? 1 : page;
        }

        public String getContentHtml() {
            return contentHtml;
        }


        @Override
        public String toString() {
            return "HeaderInfo{" +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + time + '\'' +
                    ", tag='" + tag + '\'' +
                    ", comment='" + comment + '\'' +
                    ", page=" + page +
                    ", title='" + title + '\'' +
                    ", contentHtml='" + contentHtml + '\'' +
                    ", postScripts=" + postScripts +
                    '}';
        }

        @Override
        public boolean isHeaderItem() {
            return true;
        }

        public static class PostScript {
            @Pick("span.fade")
            private String header;
            @Pick(value = "div.topic_content", attr = Attrs.HTML)
            private String content;

            public String getHeader() {
                return header;
            }

            public String getContent() {
                return content;
            }

            @Override
            public String toString() {
                return "PostScript{" +
                        "header='" + header + '\'' +
                        ", content='" + content + '\'' +
                        '}';
            }
        }
    }

    public static class Reply implements Item {
        @Pick("div.reply_content")
        private String replyContent;
        @Pick("strong a.dark[href^=/member]")
        private String userName;
        @Pick(value = "img.avatar", attr = "src")
        private String avatar;
        @Pick("span.fade.small:contains(前)")
        private String time;
        @Pick("span.fade.small:contains(♥)")
        private String love;
        @Pick("span.no")
        private int floor;

        public String getFloor() {
            return floor + "楼";
        }

        @Override
        public String toString() {
            return "Reply{" +
                    "replyContent='" + replyContent + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", time='" + time + '\'' +
                    ", love='" + love + '\'' +
                    ", floor='" + floor + '\'' +
                    '}';
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getLove() {
            return love;
        }

        public void setLove(String love) {
            this.love = love;
        }

        @Override
        public boolean isHeaderItem() {
            return false;
        }
    }

    public interface Item {
        boolean isHeaderItem();
    }

}
