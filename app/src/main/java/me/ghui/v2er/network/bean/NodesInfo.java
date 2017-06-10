package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 27/05/2017.
 * https://www.v2ex.com/go/python
 */

@Pick("div.content")
public class NodesInfo {

    @Pick("div.fr.f12 > strong.gray")
    private int total;
    @Pick("a[href^=/unfavorite/node]")
    private String actionText;
    @Pick("div.cell")
    private List<Item> items;

    public int getTotal() {
        return total;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasStared() {
        return !Utils.isEmpty(actionText);
    }

    @Override
    public String toString() {
        return "NodesInfo{" +
                "total=" + total +
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
            if (Utils.isEmpty(clickedAndContentLength)) {
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
            if (Utils.isEmpty(clickedAndContentLength)) return 0;
            else {
                clickedAndContentLength = clickedAndContentLength.trim();
                String result = clickedAndContentLength.substring(0, clickedAndContentLength.lastIndexOf("•")).trim();
                result = result.split(" ")[1].trim();
                return Integer.parseInt(result);
            }
        }
    }
}
