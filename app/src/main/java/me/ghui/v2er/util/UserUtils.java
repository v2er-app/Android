package me.ghui.v2er.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import es.dmoral.prefs.Prefs;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.bean.UserInfo;

/**
 * Created by ghui on 30/04/2017.
 */

public class UserUtils {

    private static final String USER_INFO_KEY = Constants.PACKAGE_NAME + "user_info_key";

    public static UserInfo getUserInfo() {
        String json = Prefs.with(App.get()).read(USER_INFO_KEY);
        if (Check.notEmpty(json)) {
            return APIService.gson().fromJson(json, UserInfo.class);
        }
        return null;
    }

    public static String getUserName() {
        if (!isLogin()) return null;
        return getUserInfo().getUserName();
    }

    public static String getUserID() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return "";
        return userInfo.getId();
    }

    public static boolean isLogin() {
        return getUserInfo() != null;
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
            APIService.get().userInfo(userName)
                    .compose(RxUtils.io_main())
                    .subscribe(new GeneralConsumer<UserInfo>() {
                        @Override
                        public void onConsume(UserInfo userInfo) {
                            if (userInfo != null && !TextUtils.isEmpty(userInfo.getId())) {
                                // update to use large avatar
                                // https://cdn.v2ex.com/avatar/c6f7/ffa0/161290_mini.png?m=1506764621
                                String largeAvatar = userInfo.getAvatar().replace("mini.png", "large.png");
                                userInfo.setAvatar(largeAvatar);
                                L.d("save rich userInfo, username: "
                                        + userInfo.getUserName()
                                        + ", userId: " + userInfo.getId());
                                saveLogin(userInfo, false);
                            }
                        }
                    });
        }
    }

    public static void clearLogin() {
        //1. crear cookie
        APIService.cookieJar().clearCookie();
        //2. crear userInfo
        Prefs.with(App.get()).remove(USER_INFO_KEY);
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

    public static boolean isPro() {
        return true;
    }

    public static boolean isGhui() {
        String currentUser = getUserName();
        if (Check.isEmpty(currentUser)) return false;
        return "ghui".contentEquals(currentUser)
                || "ghuiii".contentEquals(currentUser);
    }

}
