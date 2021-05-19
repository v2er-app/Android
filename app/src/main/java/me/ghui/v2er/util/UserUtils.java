package me.ghui.v2er.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import es.dmoral.prefs.Prefs;
import me.ghui.v2er.bus.event.PayResultEvent;
import me.ghui.v2er.module.pay.PayUtil;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.bean.UserInfo;

/**
 * Created by ghui on 30/04/2017.
 */

public class UserUtils {

    private static final String USER_INFO_KEY = Constants.PACKAGE_NAME + "user_info_key";
    private static final String IS_Google_PRO_KEY = Constants.PACKAGE_NAME + ".is_pro_google";
    private static final String IS_Wechat_PRO_KEY = Constants.PACKAGE_NAME + ".is_pro_wechat";

    public static UserInfo getUserInfo() {
        String json = Prefs.with(App.get()).read(USER_INFO_KEY);
        if (Check.notEmpty(json)) {
            return APIService.gson().fromJson(json, UserInfo.class);
        }
        return null;
    }

    public static String getUserBasicInfo() {
        UserInfo userInfo = getUserInfo();
        return userInfo == null ? null : userInfo.getUserBasicInfo();
    }

    public static String getUserName() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return "";
        else return userInfo.getUserName();
    }

    public static String getUserID() {
        // Fix it todo
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return "";
        return userInfo.getId();
    }

    public static boolean isLogin() {
        return Check.notEmpty(Prefs.with(App.get()).read(USER_INFO_KEY));
    }


    public static void saveLogin(UserInfo userInfo) {
        saveLogin(userInfo, true);
    }

    private static void saveLogin(UserInfo userInfo, boolean checkWechatPay) {
        if (userInfo == null || Check.isEmpty(userInfo.getUserName())) return;
        String json = APIService.gson().toJson(userInfo);
        Prefs.with(App.get()).write(USER_INFO_KEY, json);

        String userName = userInfo.getUserName();
        if (TextUtils.isEmpty(userInfo.getId())) {
            // TODO: 2021/5/19
            APIService.get().userInfo(userName)
                    .compose(RxUtils.io_main())
                    .subscribe(new GeneralConsumer<UserInfo>() {
                        @Override
                        public void onConsume(UserInfo userInfo) {
                            if (userInfo != null && !TextUtils.isEmpty(userInfo.getId())) {
                                L.d("save rich userInfo, username: "
                                        + userInfo.getUserName()
                                        + ", userId: " + userInfo.getId());
                                saveLogin(userInfo, false);
                            }
                        }
                    });
        }
        if (checkWechatPay && !UserUtils.isGooglePro()) {
            PayUtil.checkIsWechatPro();
        }
    }

    public static void clearLogin() {
        //1. crear cookie
        APIService.cookieJar().clearCookie();
        //2. crear userInfo
        Prefs.with(App.get()).remove(USER_INFO_KEY);
        // 3. clear wechat pro info
        clearWechatProInfo();
    }

    public static boolean notLoginAndProcessToLogin(boolean finishCurrentPage, Context context) {
        if (!isLogin()) {
            Voast.show("登录后才能进行此操作");
            Navigator.from(context).to(LoginActivity.class).start();
            if (context instanceof Activity && finishCurrentPage) {
                ((Activity) context).finish();
            }
            return true;
        }
        return false;
    }

    public static void saveIsGooglePro(boolean isGooglePro) {
        Pref.save(IS_Google_PRO_KEY, isGooglePro);
    }

    public static void saveIsWechatPro(boolean isWechatPro) {
        Pref.save(IS_Wechat_PRO_KEY, isWechatPro);
    }

    public static boolean isPro() {
        return isGooglePro() || isWechatPro();
    }

    public static boolean isGooglePro() {
        return Pref.readBool(IS_Google_PRO_KEY);
    }

    public static boolean isWechatPro() {
        if (!UserUtils.isLogin()) return false;
        return Pref.readBool(IS_Wechat_PRO_KEY);
    }

    private static void clearWechatProInfo() {
        Prefs.with(App.get()).writeBoolean(IS_Wechat_PRO_KEY, false);
    }

}
