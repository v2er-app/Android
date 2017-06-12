package me.ghui.v2er.network.bean;

import java.util.List;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.Constants;

/**
 * Created by ghui on 10/05/2017.
 */

@Pick("div.box")
public class NotificationInfo {

    @Pick("strong.fade")
    private String page = "1/1"; // 1/20
    @Pick("div.cell")
    private List<Reply> replies;

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "page='" + page + '\'' +
                ", replies=" + replies +
                '}';
    }

    public int getPage() {
        return Integer.parseInt(page.split("/")[1]);
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public static class Reply {
        @Pick("a[href^=/member/] strong")
        private String name;
        @Pick(value = "a[href^=/member/] img", attr = Attrs.SRC)
        private String avatar;
        @Pick(value = "span.fade")
        private String title;
        @Pick(value = "a[href^=/t/]", attr = Attrs.HREF)
        private String link;
        @Pick("div.payload")
        private String content;
        @Pick("span.snow")
        private String time;

        @Override
        public String toString() {
            return "Reply{" +
                    "name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", content='" + content + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }

        public String getLink() {
            return Constants.BASE_URL + link;
        }


        public String getTitle() {
            if (PreConditions.notEmpty(title))
                return title.replaceFirst(name, "").trim();
            return title;
        }

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return Constants.HTTPS_SCHEME + avatar;
        }

        public String getContent() {
            return content;
        }

        public String getTime() {
            return time;
        }
    }

}
