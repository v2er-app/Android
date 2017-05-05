package me.ghui.v2ex.network.bean;

import java.util.List;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 04/05/2017.
 */

public class TopicInfo {

    @Select("h1")
    private String title;
    @Select("div.markdown_body")
    private String contentHtml;
    @Select("div.cell[id^=r_]")
    private List<Reply> replies;


    public static class Reply {
        @Select("div.reply_content")
        private String replyContent;
    }

}
