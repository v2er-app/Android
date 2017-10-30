package me.ghui.v2er.network.bean;

import me.ghui.fruit.annotations.Pick;
import me.ghui.toolbox.android.Check;

/**
 * Created by ghui on 16/08/2017.
 */
@Pick("form[method=post]")
public class TwoStepLoginInfo extends BaseInfo {
    @Pick("tr:first-child")
    private String title;
    @Pick(value = "input[type=hidden]", attr = "value")
    private String once;

    @Override
    public boolean isValid() {
        return Check.notEmpty(once) && Check.notEmpty(title) && title.contains("两步验证");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOnce() {
        return once;
    }

    public void setOnce(String once) {
        this.once = once;
    }

    @Override
    public String toString() {
        return "TwoStepLoginInfo{" +
                "title='" + title + '\'' +
                ", once='" + once + '\'' +
                '}';
    }
}
