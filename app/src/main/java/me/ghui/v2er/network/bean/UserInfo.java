package me.ghui.v2er.network.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ghui on 03/05/2017.
 */

public class UserInfo {
    /*
    "status" : "found",
    "id" : 161290,
    "url" : "http://www.v2ex.com/member/ghui",
    "username" : "ghui",
    "website" : "https://ghui.me",
    "twitter" : "",
    "psn" : "",
    "github" : "",
    "btc" : "",
    "location" : "",
    "tagline" : "",
    "bio" : "",
    "avatar_mini" : "//v2ex.assets.uxengine.net/avatar/c6f7/ffa0/161290_mini.png?m=1492488139",
    "avatar_normal" : "//v2ex.assets.uxengine.net/avatar/c6f7/ffa0/161290_normal.png?m=1492488139",
    "avatar_large" : "//v2ex.assets.uxengine.net/avatar/c6f7/ffa0/161290_large.png?m=1492488139",
    "created" : 1456813618
     */

    private String status;
    private String id;
    private String userName;
    private String website;
    private String twitter;
    private String psn;
    private String github;
    private String btc;
    private String location;
    private String tagline;
    private String bio;
    @SerializedName("avatar_large")
    private String avatar;
    private String created;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getPsn() {
        return psn;
    }

    public void setPsn(String psn) {
        this.psn = psn;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getBtc() {
        return btc;
    }

    public void setBtc(String btc) {
        this.btc = btc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return "https:" + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public static UserInfo build(String userName, String avatar) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setAvatar(avatar);
        return userInfo;
    }
}
