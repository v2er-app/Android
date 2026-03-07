package me.ghui.v2er.util;

import me.ghui.v2er.network.Constants;

/**
 * Created by ghui on 14/06/2017.
 */

public interface RefererUtils {

    String TINY_REFER = Constants.BASE_URL + "/mission/daily";

    public static String topicReferer(String topicId) {
        return Constants.getBaseUrl() + "/t/" + topicId;
    }

    public static String userReferer(String username) {
        return Constants.getBaseUrl() + "/member/" + username;
    }
}
