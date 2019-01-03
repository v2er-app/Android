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


    /**
     * 获取 mimeType
     */
    public static String getMimeType(String url) {
        if (url.endsWith(".png") || url.endsWith(".PNG")) {
            return "data:image/png;base64,";
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".JPG") || url.endsWith(".JPEG")) {
            return "data:image/jpg;base64,";
        } else if (url.endsWith(".gif") || url.endsWith(".GIF")) {
            return "data:image/gif;base64,";
        } else {
            return "";
        }
    }

    public static boolean isImg(String url) {
        String REGULAR_RULE = "(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\\.(?:jpg|jpeg|gif|png|JPG|JPEG|GIF|PNG))(?:\\?([^#]*))?(?:#(.*))?";
        return url.matches(REGULAR_RULE);
    }
}
