package me.ghui.v2er.network.bean;


import me.ghui.fruit.annotations.Pick;

/**
 * Created by ghui on 03/05/2017.
 */

public class LoginResultInfo {

    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=/avatar/]", attr = "src")
    private String avatar;


    public String getUserName() {
        return userLink.split("/")[2];
    }

    public String getAvatar() {
        return avatar.replace("normal.png", "large.png");
    }

    @Override
    public String toString() {
        return "LoginResultInfo{" +
                "userLink='" + userLink + '\'' +
                ", avatar='" + avatar + '\'' +
                ", userName='" + getUserName() +
                '}';
    }
}
