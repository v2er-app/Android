package me.ghui.v2er.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;

import java.net.URI;
import java.net.URISyntaxException;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.general.WapActivity;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

/**
 * Created by ghui on 02/06/2017.
 * 1. 站内的url用webview打开，共享cookie
 * 2. 站外的url用chrome custom tab
 */

public class UrlInterceptor {

    /**
     * @param url
     * @param context
     * @param onlyCheck            仅仅是检查拦截情况
     * @param forchOpenedInWebview 强制用webview打开
     * @return
     */
    public static boolean intercept(String url, Context context, boolean onlyCheck, boolean forchOpenedInWebview) {
        boolean result = false;
        if (Check.isEmpty(url)) return result;

        if (url.startsWith("mailto:")) {
            //send email
            String title = UserUtils.isPro() ? "【From V2er Pro】" : "【From V2er】";
            Utils.sendEmail(context, url, title);
            return true;
        }

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
        if (uri == null) {
            Voast.show("Invalid Url");
            return false;
        }
        String host = uri.getHost();
        if (Check.isEmpty(host)) return false;
        if (!forchOpenedInWebview && !host.contains(Constants.HOST_NAME)) {
            // 1. 外站
            boolean useBuiltinBrowser = Pref.readBool(R.string.pref_key_use_builtin_browser);
            if (useBuiltinBrowser) {
                // 使用内置浏览器打开外站链接
                WapActivity.open(url, context, true);
            } else {
                // 使用 Chrome Custom Tab 打开外站链接
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                        .enableUrlBarHiding()
                        .setShowTitle(true)
                        .addDefaultShareMenuItem()
                        .setStartAnimations(context, R.anim.open_enter_slide, R.anim.open_exit_slide)
                        .setExitAnimations(context, R.anim.close_enter_slide, R.anim.close_exit_slide)
                        .build();
                try {
                    customTabsIntent.launchUrl(context, Uri.parse(url));
                } catch (ActivityNotFoundException e) {
                    WapActivity.open(url, context, true);
                }
            }
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
            L.d("userName: " + userName);
            Navigator.from(context)
                    .to(UserHomeActivity.class)
                    .putExtra(UserHomeActivity.USER_NAME_KEY, userName)
                    .start();
            result = true;
        } else {
            //open url in a default webview
            if (!onlyCheck) {
                WapActivity.open(url, context, forchOpenedInWebview);
            }
            result = false;
        }
        return result;
    }


    /**
     * @param url
     * @param context
     */
    public static void openWapPage(String url, Context context) {
        intercept(url, context, false, false);
    }


    public static void openWapPage(String url, Context context, boolean forchOpenedInWebview) {
        intercept(url, context, false, forchOpenedInWebview);
    }
}
