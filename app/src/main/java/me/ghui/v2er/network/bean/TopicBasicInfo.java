package me.ghui.v2er.network.bean;

import java.io.Serializable;

/**
 * Created by ghui on 25/06/2017.
 */

public class TopicBasicInfo implements Serializable {
    private String avatar;
    private String author;
    private String tag;
    private String title;
    private int commentNum;

    public String getAvatar() {
        return avatar;
    }

    public String getAuthor() {
        return author;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public int getCommentNum() {
        return commentNum;
    }

    private TopicBasicInfo(Builder builder) {
        this.title = builder.title;
        this.avatar = builder.avatar;
        this.author = builder.author;
        this.tag = builder.tag;
        this.commentNum = builder.commentNum;
    }

    public static class Builder {
        private String title;
        private String avatar;
        private String author;
        private String tag;
        private int commentNum;

        public Builder(String title, String avatar) {
            this.title = title;
            this.avatar = avatar;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder commentNum(int commentNum) {
            this.commentNum = commentNum;
            return this;
        }

        public TopicBasicInfo build() {
            return new TopicBasicInfo(this);
        }

    }
}
