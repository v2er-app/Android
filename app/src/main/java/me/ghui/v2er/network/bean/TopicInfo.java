package me.ghui.v2er.network.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo {

    @Pick("div.box")
    private HeaderInfo headerInfo;
    @Pick("div[id^=r_]")
    private List<Reply> replies;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;

    private List<Item> items;

    public String getOnce() {
        return once;
    }

    public Map<String, String> toReplyMap(String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    /**
     * 加载分页后的数据
     *
     * @param isLoadMore
     * @return
     */
    public List<Item> getItems(boolean isLoadMore) {
        if (items == null) {
            items = new ArrayList<>(Utils.listSize(replies) + 1);
        } else {
            items.clear();
        }

        if (!isLoadMore) {
            items.add(headerInfo);
        }
        items.addAll(replies);
        return items;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    public int getTotalPage() {
        return headerInfo.getPage();
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "headerInfo=" + headerInfo +
                ", replies=" + replies +
                ", once=" + once +
                '}';
    }

    public static class HeaderInfo implements Item {
        @Pick(value = "img.avatar", attr = "src")
        private String avatar;
        @Pick("small.gray a")
        private String userName;
        @Pick(value = "small.gray", attr = "ownText")
        private String time;
        @Pick("a[href^=/go]")
        private String tag;
        @Pick(value = "a[href^=/go]", attr = Attrs.HREF)
        private String tagLink;
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
        //        @Pick(value = "a.tb[href^=/favorite]", attr = Attrs.HREF)
        @Pick(value = "a[href^=/favorite]", attr = Attrs.HREF)
        private String favoriteLink;

        public String getT() {
            if (PreConditions.isEmpty(favoriteLink)) {
                return null;
            }
            return UriUtils.getParamValue(Constants.BASE_URL + favoriteLink, "t");
        }

        public String getCommentNum() {
            if (PreConditions.isEmpty(comment)) return "评论0";
            return "评论" + comment.split(" ")[0];
        }

        public String getTagLink() {
            return tagLink;
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
                    "tId='" + getT() + '\'' +
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
        @Pick("div.thank_area.thanked")
        private String alreadyThanked;
        @Pick(attr = "id")
        private String replyId;

        public String getFloor() {
            return floor + "楼";
        }

        public String getReplyId() {
            if (PreConditions.isEmpty(replyId)) return null;
            try {
                return replyId.substring(replyId.indexOf("_") + 1);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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

        public boolean hadThanked() {
            return PreConditions.notEmpty(alreadyThanked);
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
            return Constants.HTTPS_SCHEME + avatar;
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

        public int getLove() {
            int loveCount = 0;
            if (PreConditions.isEmpty(love)) {
                return loveCount;
            }
            try {
                loveCount = Integer.parseInt(love.substring(2));
            } catch (Exception e) {
                e.printStackTrace();
                loveCount = 0;
            }
            return loveCount;
        }

        public void setLove(int count) {
            this.love = "♥ " + count;
        }

        public void updateThanks(boolean isSuccess) {
            if (isSuccess) {
                alreadyThanked = "感谢已发送";
                this.love = "♥ " + (getLove() + 1);
            }
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
