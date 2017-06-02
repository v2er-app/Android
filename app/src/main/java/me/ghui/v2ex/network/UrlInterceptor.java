package me.ghui.v2ex.network;

import android.content.Context;

import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.util.UriUtils;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 02/06/2017.
 */

public class UrlInterceptor {

    public static boolean intercept(String url, Context context) {
        boolean result = false;
        if (Utils.isEmpty(url)) return result;
        if (!url.startsWith(Constants.HTTPS_SCHEME) && !url.startsWith(Constants.HTTP_SCHEME)) {
            //url is path
            if (url.startsWith("/")) {
                url = Constants.BASE_URL + url;
            } else {
                url = Constants.BASE_URL + "/" + url;
            }
        }
        //now has a complete url
        if (url.contains("/t/")) {
            //topic link
            TopicActivity.open(url, context);
            result = true;
        } else if (url.contains("/go/")) {
            //node link
            int page;
            try {
                page = Integer.parseInt(UriUtils.getParamValue(url, "p"));
            } catch (Exception e) {
                e.printStackTrace();
                page = 1;
            }
            NodeTopicActivity.open(url, page, context);
            result = true;
        } else {
            //open url in a default webview
            // TODO: 03/06/2017  
            result = true;
        }
        return result;
    }


}
