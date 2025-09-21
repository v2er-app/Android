package me.ghui.v2er.network.bean;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.AvatarUtils;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo extends BaseInfo {
    @Pick("div#Wrapper")
    private HeaderInfo headerInfo;
    @Pick("div.content div.box")
    private ContentInfo contentInfo;
    @Pick("div.problem")
    private Problem problem;
    @Pick("div[id^=r_]")
    private List<Reply> replies;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "meta[property=og:url]", attr = "content")
    private String topicLink;
    @Pick(value = "a[onclick*=/report/topic/]", attr = "onclick")
    private String reportLink;
    @Pick(value = "div.content div.box div.inner span.fade")
    private String hasRePortStr;
    @Pick(value = "a[onclick*=/fade/topic/]", attr = "onclick")
    private String fadeStr;
    @Pick(value = "a[onclick*=/sticky/topic/]", attr = "onclick")
    private String stickyStr;

    private List<Item> items;

    public Problem getProblem() {
        return problem;
    }

    public String getTopicLink() {
        return topicLink;
    }

    public boolean canSticky() {
        return Check.notEmpty(stickyStr());
    }

    public boolean canfade() {
        return Check.notEmpty(fadeUrl());
    }

    public boolean hasReported() {
        return UserUtils.isLogin() && !TextUtils.isEmpty(hasRePortStr) && hasRePortStr.contains("已对本主题进行了报告");
    }

    public boolean hasReportPermission() {
        return hasReported() || !TextUtils.isEmpty(reportLink);
    }

    public String reportUrl() {
        if (hasReported()) return null;
        //if (confirm('你确认需要报告这个主题？')) { location.href = '/report/topic/390988?t=1456813618'; }
        int sIndex = reportLink.indexOf("/report/topic/");
        int eIndex = reportLink.lastIndexOf("'");
        return reportLink.substring(sIndex, eIndex);
    }

    public String fadeUrl() {
        if (TextUtils.isEmpty(fadeStr)) return null;
        int sIndex = fadeStr.indexOf("/fade/topic/");
        int eIndex = fadeStr.lastIndexOf("'");
        return fadeStr.substring(sIndex, eIndex);
    }

    public String stickyStr() {
        if (TextUtils.isEmpty(stickyStr)) return null;
        int sIndex = stickyStr.indexOf("/sticky/topic/");
        int eIndex = stickyStr.lastIndexOf("'");
        return stickyStr.substring(sIndex, eIndex);
    }

    public String getOnce() {
        return once;
    }

    public Map<String, String> toReplyMap(String content) {
        HashMap<String, String> map = new HashMap<>();
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    /**
     * 加载分页后的数据
     *
     * @param isLoadMore
     * @param isInOrder  是否是正序加载
     * @return
     */
    public List<Item> getItems(boolean isLoadMore, boolean isInOrder) {
        return getItems(isLoadMore, isInOrder, me.ghui.v2er.general.ReplySortMode.BY_TIME);
    }

    /**
     * 加载分页后的数据
     *
     * @param isLoadMore
     * @param isInOrder   是否是正序加载
     * @param sortMode    回复排序模式
     * @return
     */
    public List<Item> getItems(boolean isLoadMore, boolean isInOrder, me.ghui.v2er.general.ReplySortMode sortMode) {
        if (items == null) {
            items = new ArrayList<>(Utils.listSize(replies) + 2);
        } else {
            items.clear();
        }

        if (!isInOrder && Check.notEmpty(replies) && !hasReversed()) {
            Collections.reverse(replies);
        }

        if (!isLoadMore) {
            // 第一次加载需要将头部、内容信息加入列表
            items.add(headerInfo);
            if (contentInfo.isValid()) {
                items.add(contentInfo);
            }
        }
        String owner = headerInfo.getUserName();
        if (Check.notEmpty(replies)) {
            for (Reply reply : replies) {
                reply.setOwner(owner);
            }
            
            // Parse mentions and calculate threading levels
            processMentionsAndThreading(replies);
            
            // Apply sorting based on mode
            List<Reply> sortedReplies = new ArrayList<>(replies);
            if (sortMode == me.ghui.v2er.general.ReplySortMode.BY_POPULARITY) {
                // Sort by love count (popularity) in descending order, then by floor for stable sort
                Collections.sort(sortedReplies, (r1, r2) -> {
                    int loveCompare = Integer.compare(r2.getLove(), r1.getLove());
                    if (loveCompare != 0) {
                        return loveCompare;
                    }
                    // If love count is equal, sort by floor for stable ordering
                    return Integer.compare(r1.floor, r2.floor);
                });
            }
            
            items.addAll(sortedReplies);
        }
        return items;
    }

    /**
     * replies是否已经reversed
     *
     * @return
     */
    private boolean hasReversed() {
        if (replies.size() >= 2) {
            return replies.get(0).floor > replies.get(1).floor;
        }
        return true;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    /**
     * Process @mentions in replies and calculate threading levels
     * @param replies List of replies to process
     */
    private void processMentionsAndThreading(List<Reply> replies) {
        if (replies == null || replies.isEmpty()) return;
        
        // Create a map of username to reply for quick lookup
        Map<String, Reply> userToLatestReply = new HashMap<>();
        
        for (Reply reply : replies) {
            // Parse @mentions from reply content
            List<String> mentions = extractMentions(reply.getReplyContent());
            reply.setMentionedUsers(mentions);
            
            // Calculate indent level based on mentions
            int maxIndentLevel = 0;
            for (String mentionedUser : mentions) {
                Reply mentionedReply = userToLatestReply.get(mentionedUser);
                if (mentionedReply != null) {
                    // This reply is responding to another reply, increase indent
                    maxIndentLevel = Math.max(maxIndentLevel, mentionedReply.getIndentLevel() + 1);
                }
            }
            reply.setIndentLevel(maxIndentLevel);
            
            // Update the latest reply for this user
            userToLatestReply.put(reply.getUserName(), reply);
        }
    }

    /**
     * Extract @username mentions from reply content
     * @param content HTML content of the reply
     * @return List of mentioned usernames
     */
    private List<String> extractMentions(String content) {
        List<String> mentions = new ArrayList<>();
        if (Check.isEmpty(content)) return mentions;
        
        // Use regex to find @username patterns
        // V2EX format: @username or @<a href="/member/username">username</a>
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "@(?:<a[^>]*href=\"/member/([^\"]+)\"[^>]*>([^<]+)</a>|([a-zA-Z0-9_\\-]+))"
        );
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String username = matcher.group(1); // From href
            if (username == null) {
                username = matcher.group(3); // Direct @username
            }
            if (username != null && !mentions.contains(username)) {
                mentions.add(username);
            }
        }
        
        return mentions;
    }
        return headerInfo;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public int getTotalPage() {
        return headerInfo.getTotalPage();
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "topicLink=" + topicLink +
                ", headerInfo=" + headerInfo +
                ", replies=" + replies +
                ", once=" + once +
                '}';
    }

    @Override
    public boolean isValid() {
        if (headerInfo == null) return false;
        return headerInfo.isValid();
    }

    public static class Problem implements Serializable {
        @Pick(attr = Attrs.OWN_TEXT)
        private String title;
        @Pick("ul li")
        private List<String> tips;

        public boolean isEmpty() {
            return Check.isEmpty(tips) && Check.isEmpty(title);
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

    public interface Item extends Serializable {
        boolean isHeaderItem();

        boolean isContentItem();

        boolean isSelf();

        String getUserName();

        String getAvatar();
    }

    public static class ContentInfo extends BaseInfo implements Item {
        @Pick(attr = Attrs.HTML)
        private String html;

        private String formatedHtml;

        /**
         * 得到处理后的html, 移除最后一个element(时间，收藏，等不需要显示的信息)
         *
         * @return
         */
        public String getFormattedHtml() {
            if (formatedHtml != null) return formatedHtml;
            Document parentNode = Jsoup.parse(html);
            parentNode.getElementsByClass("header").remove();
            parentNode.getElementsByClass("inner").remove();
            if ("".equals(parentNode.text())
                    && parentNode.getElementsByClass("embedded_video_wrapper") == null) {
                formatedHtml = null;
                return formatedHtml;
            } else {
                formatedHtml = parentNode.body().html();
            }
            return formatedHtml;
        }

        @Override
        public boolean isValid() {
            return !TextUtils.isEmpty(getFormattedHtml());
        }

        @Override
        public boolean isHeaderItem() {
            return false;
        }

        @Override
        public boolean isContentItem() {
            return true;
        }

        @Override
        public boolean isSelf() {
            return false;
        }

        @Override
        public String getUserName() {
            return null;
        }

        @Override
        public String getAvatar() {
            return null;
        }
    }

    public static class HeaderInfo extends BaseInfo implements Item {
        @Pick(value = "div.box img.avatar", attr = "src")
        private String avatar;
        @Pick("div.box small.gray a")
        private String userName;
        @Pick(value = "div.box small.gray", attr = "ownText")
        private String time;
        @Pick("div.box a[href^=/go]")
        private String tag;
        @Pick(value = "div.box a[href^=/go]", attr = Attrs.HREF)
        private String tagLink;
        @Pick("div.cell span.gray:contains(回复)")
        private String comment;
        @Pick("div.box a.page_normal:last-child")
        private int page;
        @Pick("div.box span.page_current")
        private int currentPage;
        @Pick("div.box h1")
        private String title;
        @Pick(value = "div.box a[href*=favorite/]", attr = Attrs.HREF)
        private String favoriteLink;
        @Pick("div.box div[id=topic_thank]")
        private String thankedText;// 感谢已发送
        @Pick("div.box div.inner div#topic_thank")
        private String canSendThanksText;
        private String computedTime;
        @Pick("div.box div.header a.op")
        private String appendTxt;

        public HeaderInfo() {
        }

        private HeaderInfo(TopicBasicInfo basicInfo) {
            this.avatar = basicInfo.getAvatar();
            this.title = basicInfo.getTitle();
            this.userName = basicInfo.getAuthor();
            this.tag = basicInfo.getTag();
            this.tagLink = basicInfo.getTagLink();
        }

        public static HeaderInfo build(TopicBasicInfo basicInfo) {
            return new HeaderInfo(basicInfo);
        }

        @Override
        public boolean isValid() {
            return Check.notEmpty(userName, tag);
        }


        public boolean canAppend() {
            return Check.notEmpty(appendTxt) && appendTxt.equals("APPEND");
        }


        /**
         * new user can't send thanks
         *
         * @return
         */
        public boolean canSendThanks() {
            return Check.notEmpty(canSendThanksText);
        }

        public boolean hadThanked() {
            return Check.notEmpty(thankedText) && thankedText.contains("已发送");
        }

        public String getFavoriteLink() {
            return favoriteLink;
        }

        public void setFavoriteLink(String favoriteLink) {
            this.favoriteLink = favoriteLink;
        }

        public void updateThxStatus(boolean thxed) {
            thankedText = thxed ? "感谢已发送" : null;
        }

        public void updateStarStatus(boolean stared) {
            if (stared) {
                favoriteLink = favoriteLink.replace("/favorite/", "/unfavorite/");
            } else {
                favoriteLink = favoriteLink.replace("/unfavorite/", "/favorite/");
            }
        }

        public String getT() {
            if (Check.isEmpty(favoriteLink)) {
                return null;
            }
            return UriUtils.getParamValue(Constants.BASE_URL + favoriteLink, "t");
        }

        public boolean hadStared() {
            if (Check.isEmpty(favoriteLink) || !favoriteLink.contains("unfavorite/")) {
                return false;
            }
            return true;
        }

        public String getCommentNum() {
            if (Check.isEmpty(comment)) return "";
            return comment.split(" ")[0];
        }

        public String getTagLink() {
            return tagLink;
        }

        public String getTag() {
            return tag;
        }

        public String getTime() {
            try {
                if (Check.isEmpty(computedTime) && Check.notEmpty(time) && time.contains("·")) {
                    computedTime = time.split("·")[0].trim().substring(6).replaceAll(" ", "").trim();
                    if (computedTime.contains("-") && computedTime.contains("+")) {
                        computedTime = computedTime.substring(0, 10);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return computedTime;
        }

        public int getViewCount() {
            try {
                String count = time.split("·")[1].trim();
                return Integer.parseInt(count.substring(0, count.indexOf(" ")));
            } catch (Exception e) {
                return 0;
            }
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return AvatarUtils.adjustAvatar(avatar);
        }

        public String getTitle() {
            return title;
        }

        public int getTotalPage() {
            return Math.max(page, currentPage);
        }


        @Override
        public String toString() {
            return "HeaderInfo{" +
                    "tId='" + getT() + '\'' +
                    "avatar='" + avatar + '\'' +
                    ", userName='" + userName + '\'' +
                    ", time='" + getTime() + '\'' +
                    ", tag='" + tag + '\'' +
                    ", comment='" + comment + '\'' +
                    ", page=" + page +
                    ", title='" + title + '\'' +
                    '}';
        }

        @Override
        public boolean isHeaderItem() {
            return true;
        }

        @Override
        public boolean isContentItem() {
            return false;
        }

        @Override
        public boolean isSelf() {
            try {
                return userName.equals(UserUtils.getUserInfo().getUserName());
            } catch (NullPointerException e) {
                return false;
            }
        }


    }

    public static class Reply implements Item {
        @Pick(value = "div.reply_content", attr = Attrs.INNER_HTML)
        private String replyContent;
        @Pick("strong a.dark[href^=/member]")
        private String userName;
        @Pick(value = "img.avatar", attr = "src")
        private String avatar;
        @Pick("span.fade.small:not(:contains(♥))")
        private String time;
        @Pick("span.small.fade:has(img)")
        private String love;
        @Pick("span.no")
        private int floor;
        @Pick("div.thank_area.thanked")
        private String alreadyThanked;
        @Pick(attr = "id")
        private String replyId;
        private boolean isOwner = false;
        // Threading support for 楼中楼 functionality
        private List<String> mentionedUsers = new ArrayList<>();
        private int indentLevel = 0;

        public boolean isOwner() {
            return isOwner;
        }

        public void setOwner(String owner) {
            if (Check.notEmpty(userName) && Check.notEmpty(owner) && owner.equals(userName)) {
                isOwner = true;
            } else {
                isOwner = false;
            }
        }

        public String getFloor() {
            return floor + "楼";
        }

        public String getReplyId() {
            if (Check.isEmpty(replyId)) return null;
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
            return Check.notEmpty(alreadyThanked);
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
            return AvatarUtils.adjustAvatar(avatar);
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
            if (Check.isEmpty(love)) {
                return loveCount;
            }
            try {
                loveCount = Integer.parseInt(love);
            } catch (Exception e) {
                e.printStackTrace();
                loveCount = 0;
            }
            return loveCount;
        }

        public void updateThanks(boolean isSuccess) {
            if (isSuccess) {
                alreadyThanked = "感谢已发送";
                this.love = getLove() + 1 + "";
            }
        }

        @Override
        public boolean isHeaderItem() {
            return false;
        }

        @Override
        public boolean isContentItem() {
            return false;
        }

        @Override
        public boolean isSelf() {
            try {
                return userName.equals(UserUtils.getUserInfo().getUserName());
            } catch (NullPointerException e) {
                return false;
            }
        }

        // Threading support methods
        public List<String> getMentionedUsers() {
            return mentionedUsers;
        }

        public void setMentionedUsers(List<String> mentionedUsers) {
            this.mentionedUsers = mentionedUsers != null ? mentionedUsers : new ArrayList<>();
        }

        public int getIndentLevel() {
            return indentLevel;
        }

        public void setIndentLevel(int indentLevel) {
            this.indentLevel = Math.max(0, Math.min(indentLevel, 3)); // Max 3 levels
        }

        public boolean hasMentions() {
            return mentionedUsers != null && !mentionedUsers.isEmpty();
        }
    }

}
