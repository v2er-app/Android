package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.Constants;

/**
 * Created by ghui on 27/05/2017.
 * https://www.v2ex.com/go/python
 */

@Pick("div.content")
public class NodeTopicInfo {

    @Pick("div.fr.f12 > strong.gray")
    private int total;
    @Pick(value = "div.fr.f12 > a", attr = Attrs.HREF)
    private String favoriteLink;
    @Pick("div.cell")
    private List<Item> items;

    public int getTotal() {
        return total;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getFavoriteLink() {
        return Constants.BASE_URL + favoriteLink;
    }

    public boolean hasStared() {
        return PreConditions.notEmpty(favoriteLink) && favoriteLink.contains("/unfavorite/node/");
    }

    public void updateStarStatus(boolean isStared) {
        if (isStared) {
            favoriteLink = favoriteLink.replace("/favorite/", "/unfavorite/");
        } else {
            favoriteLink = favoriteLink.replace("/unfavorite/", "/favorite/");
        }
    }

    @Override
    public String toString() {
        return "NodeTopicInfo{" +
                "favoriteLink=" + favoriteLink +
                ",total=" + total +
                ", items=" + items +
                '}';
    }

    public static class Item {
        @Pick(value = "img.avatar", attr = Attrs.SRC)
        private String avatar;
        @Pick("span.item_title")
        private String title;
        @Pick("span.small.fade strong")
        private String userName;
        @Pick(value = "span.small.fade", attr = Attrs.OWN_TEXT)
        private String clickedAndContentLength;
        @Pick("a.count_livid")
        private int commentNum;
        @Pick(value = "span.item_title a", attr = Attrs.HREF)
        private String topicLink;

        public String getTopicLink() {
            return topicLink;
        }

        public String getAvatar() {
            return Constants.HTTPS_SCHEME + avatar;
        }

        public String getTitle() {
            return title;
        }

        public String getUserName() {
            return userName;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public int getClickNum() {
            //  •  719 个字符  •  109 次点击
            if (PreConditions.isEmpty(clickedAndContentLength)) {
                return 0;
            } else {
                int count;
                try {
                    String result = clickedAndContentLength.substring(clickedAndContentLength.lastIndexOf("•") + 1);
                    result = result.replaceAll("[^0-9]", "");
                    count = Integer.parseInt(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    count = 0;
                }
                return count;
            }
        }

        public int getContentLength() {
            if (PreConditions.isEmpty(clickedAndContentLength)) return 0;
            else {
                clickedAndContentLength = clickedAndContentLength.trim();
                String result = clickedAndContentLength.substring(0, clickedAndContentLength.lastIndexOf("•")).trim();
                result = result.split(" ")[1].trim();
                return Integer.parseInt(result);
            }
        }
    }
}