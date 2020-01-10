package me.ghui.v2er.network.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ghui.fruit.annotations.Pick;

@Pick("div#Wrapper")
public class AppendTopicPageInfo extends BaseInfo {
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick("div.inner ul li")
    private List<Tip> tips;

    public List<Tip> getTips() {
        return tips;
    }

    public String getOnce() {
        return once;
    }

    public Map<String, String> toPostMap(String content) {
        Map<String, String> map = new HashMap<>(2);
        map.put("once", once);
        map.put("content", content);
        return map;
    }

    public static class Tip implements Serializable {
        @Pick
        public String text;

        @Override
        public String toString() {
            return "Tip{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(once);
    }

    @Override
    public String toString() {
        return "AppendTopicPageInfo{" +
                "once='" + once + '\'' +
                ", tips=" + tips +
                '}';
    }
}
