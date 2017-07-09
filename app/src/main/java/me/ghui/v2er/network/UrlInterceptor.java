package me.ghui.v2er.network;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import com.orhanobut.logger.Logger;

import java.net.URI;
import java.net.URISyntaxException;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.util.UriUtils;

/**
 * Created by ghui on 02/06/2017.
 * 1. 站内的url用webview打开，共享cookie
 * 2. 站外的url用chrome custom tab
 */

public class UrlInterceptor {

    public static boolean intercept(String url, Context context) {
        boolean result = false;
        if (PreConditions.isEmpty(url)) return result;
        if (!url.startsWith(Constants.HTTPS_SCHEME) && !url.startsWith(Constants.HTTP_SCHEME)) {
            //url is path
            if (url.startsWith("/")) {
                url = Constants.BASE_URL + url;
            } else {
                url = Constants.BASE_URL + "/" + url;
            }
        }

        //now has a complete url

        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assert uri != null;
        String host = uri.getHost();
        if (PreConditions.isEmpty(host)) return false;
        if (!host.contains(Constants.HOST_NAME)) {
            // 1. 外站
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                    .enableUrlBarHiding()
                    .setShowTitle(true)
                    .addDefaultShareMenuItem()
                    .setStartAnimations(context, R.anim.open_enter_slide, R.anim.open_exit_slide)
                    .setExitAnimations(context,R.anim.close_enter_slide, R.anim.close_exit_slide)
                    .build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
            return true;
        }

        // 2. 内站
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
        } else if (url.contains("/member/")) {
            //user page
            String userName = UriUtils.getLastSegment(url);
            Logger.d("userName: " + userName);
            Navigator.from(context)
                    .to(UserHomeActivity.class)
                    .putExtra(UserHomeActivity.USER_NAME_KEY, userName)
                    .start();
            result = true;
        } else {
            //open url in a default webview
            // TODO: 03/06/2017
            result = false;
        }
        return result;
    }


}
