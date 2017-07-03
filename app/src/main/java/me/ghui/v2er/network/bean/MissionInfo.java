package me.ghui.v2er.network.bean;


import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;

/**
 * Created by ghui on 03/05/2017.
 */

public class MissionInfo implements IBaseInfo {

    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=/avatar/]", attr = "src")
    private String avatar;


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

    @Override
    public String toString() {
        return "MissionInfo{" +
                "userLink='" + userLink + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(userLink);
    }
}
