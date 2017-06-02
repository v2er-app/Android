package me.ghui.v2ex.util;

/**
 * Created by ghui on 02/06/2017.
 */

public class UriUtils {
    public static String getLastSegment(String url) {
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }
}
