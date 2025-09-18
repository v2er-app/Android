package me.ghui.v2er.network.bean;

import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 07/08/2017.
 */

public class DailyInfo extends BaseInfo {
    @Pick(value = "[href^=/member]", attr = "href")
    private String userLink;
    @Pick(value = "img[src*=avatar/]", attr = "src")
    private String avatar;
    @Pick("h1")
    private String title;
    @Pick("div.cell:contains(已连续)")
    private String continuousLoginDayStr;
    @Pick(value = "div.cell input[type=button]", attr = "onclick")
    private String checkinUrl; //location.href = '/mission/daily/redeem?once=84830';

    public boolean hadCheckedIn() {
        return Check.notEmpty(checkinUrl) && checkinUrl.equals("location.href = '/balance';");
    }

    public String getCheckinDays() {
        if (Check.isEmpty(continuousLoginDayStr)) return "";

        // Extract consecutive days from strings like:
        // "您已连续登录 123 天"
        // "ghui 已连续签到 12 天 2024/12/25"
        // "用户161290已连续签到 5 天"

        // Use regex to find number followed by "天" (days)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*天");
        java.util.regex.Matcher matcher = pattern.matcher(continuousLoginDayStr);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Fallback to extracting all digits if pattern not found
        return Utils.extractDigits(continuousLoginDayStr);
    }


    public String getUserName() {
        if (Check.isEmpty(userLink)) {
            return null;
        }
        return userLink.split("/")[2];
    }

    public String getAvatar() {
        if (Check.isEmpty(avatar)) return null;
        return avatar.replace("normal.png", "large.png");
    }

    public String once() {
        String result = UriUtils.getParamValue(checkinUrl, "once");
        if (Check.notEmpty(result)) {
            result = result.replace("';", "");
        }
        return result;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(checkinUrl);
    }

    @Override
    public String toString() {
        return "DailyInfo{" +
                "title='" + title + '\'' +
                ", continuousLoginDay='" + getCheckinDays() + '\'' +
                ", checkinUrl='" + checkinUrl + '\'' +
                ", once='" + once() + '\'' +
                '}';
    }
}
