package me.ghui.v2er.network;

import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.bean.BaseInfo;

/**
 * Created by ghui on 16/08/2017.
 */

@Pick("div#Top")
public class LoginResultInfo extends BaseInfo {
    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=avatar/]", attr = "src")
    private String avatar;

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(avatar) && PreConditions.notEmpty(avatar);
    }

    @Override
    public String toString() {
        return "LoginResultInfo{" +
                "userLink='" + userLink + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public String getUserName() {
        if (PreConditions.isEmpty(userLink)) {
            return null;
        }
        return userLink.split("/")[2];
    }

    public String getAvatar() {
        if (PreConditions.isEmpty(avatar)) return null;
        return avatar.replace("normal.png", "large.png");
    }

}
