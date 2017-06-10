package me.ghui.v2er.util;

import android.net.Uri;

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

    public static String getParamValue(String url, String paramName) {
        return Uri.parse(url).getQueryParameter(paramName);
    }
}
