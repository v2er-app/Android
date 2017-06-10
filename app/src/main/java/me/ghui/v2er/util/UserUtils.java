package me.ghui.v2er.util;

import es.dmoral.prefs.Prefs;
import me.ghui.v2er.general.App;
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
        if (Utils.isnodempty(json)) {
            return APIService.gson().fromJson(json, UserInfo.class);
        }
        return null;
    }

    public static boolean isLogin() {
        return Utils.isnodempty(Prefs.with(App.get()).read(USER_INFO_KEY));
    }

    public static void saveLogin(UserInfo userInfo) {
        if (userInfo == null || Utils.isEmpty(userInfo.getUserName())) return;
        String json = APIService.gson().toJson(userInfo);
        Prefs.with(App.get()).write(USER_INFO_KEY, json);
    }

    public static void clearLogin() {
        //1. crear cookie
        APIService.cookieJar().clear();
        APIService.cookieJar().clearSession();
        //2. crear userInfo
        Prefs.with(App.get()).remove(USER_INFO_KEY);
    }

}
