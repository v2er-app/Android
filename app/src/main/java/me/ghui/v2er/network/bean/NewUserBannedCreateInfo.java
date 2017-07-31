package me.ghui.v2er.network.bean;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;

/**
 * Created by ghui on 31/07/2017.
 */

@Pick("div#Main")
public class NewUserBannedCreateInfo extends BaseInfo {
    @Pick(value = "div.cell", attr = Attrs.INNER_HTML)
    private String errorInfo;
    @Pick("div.header")
    private String title;

    public String getErrorInfo() {
//        return errorInfo;
        return "你的帐号刚刚注册，在你能够发帖之前，请先在 V2EX 浏览一下，了解一下这个社区的文化。\n" +
                "V2EX 是创意工作者的社区，这里能够帮助你解决问题及展示作品。关于这里的更多介绍，请点击去了解。";
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getTitle() {
//        return title;
        return "请稍等";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(errorInfo)
                && errorInfo.contains("你的帐号刚刚注册")
                && errorInfo.contains("距离能够发帖");
    }
}
