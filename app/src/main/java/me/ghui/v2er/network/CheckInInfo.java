package me.ghui.v2er.network;

import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.util.UriUtils;

/**
 * Created by ghui on 07/08/2017.
 */

@Pick("div#Wrapper")
public class CheckInInfo extends BaseInfo {
    @Pick("h1")
    private String title;
    @Pick("div.cell:last-child")
    private String continuousLoginDay;
    @Pick(value = "div.cell input[type=button]", attr = "onclick")
    private String checkinUrl; //location.href = '/mission/daily/redeem?once=84830';

    public boolean hadCheckedIn() {
        return PreConditions.notEmpty(checkinUrl) && checkinUrl.equals("location.href = '/balance';");
    }

    public String getCheckinDays() {
        return continuousLoginDay;
    }

    public String once() {
        String result = UriUtils.getParamValue(checkinUrl, "once");
        if (PreConditions.notEmpty(result)) {
            result = result.replace("';", "");
        }
        return result;
    }

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(checkinUrl);
    }

    @Override
    public String toString() {
        return "CheckInInfo{" +
                "title='" + title + '\'' +
                ", continuousLoginDay='" + continuousLoginDay + '\'' +
                ", checkinUrl='" + checkinUrl + '\'' +
                ", once='" + once() + '\'' +
                '}';
    }
}
