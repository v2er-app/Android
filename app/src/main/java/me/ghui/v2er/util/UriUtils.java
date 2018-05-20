package me.ghui.v2er.util;

import android.net.Uri;

import java.net.URL;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.network.Constants;

/**
 * Created by ghui on 02/06/2017.
 */

public class UriUtils {
    public static String getLastSegment(String url) {
        if (Check.isEmpty(url)) return "";
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static String getParamValue(String url, String paramName) {
        if (Check.isEmpty(url)) return null;
        return Uri.parse(url).getQueryParameter(paramName);
    }


    public static String checkSchema(String url) {
        if (Check.isEmpty(url)) return null;
        if (!url.startsWith("http") && !url.startsWith("https")) {
            if (url.startsWith("//")) {
                url = "http:" + url;
            } else {
                url = "http://" + url;
            }
        }
        return url;
    }

    public static boolean isValideUrl(String url) {
        try {
            URL u = new URL(url);
            u.toURI();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public static String topicLink(String id) {
        return Constants.BASE_URL + "/t/" + id;
    }
}
