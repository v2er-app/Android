package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.fruit.annotations.Pick;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo {

    @Pick("a.page_normal:last-child")
    private int page;
    @Pick("h1")
    private String title;
    @Pick("div.topic_content")
    private String contentHtml;
    @Pick("div.cell[id^=r_]")
    private List<Reply> replies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page < 1 ? 1 : page;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "page=" + getPage() +
                ", title='" + title + '\'' +
                ", contentHtml='" + contentHtml + '\'' +
                ", replies=" + replies +
                '}';
    }

    public static class Reply {
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

        @Override
        public String toString() {
            return "Reply{" +
                    "replyContent='" + replyContent + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", time='" + time + '\'' +
                    ", love='" + love + '\'' +
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
    }

}
