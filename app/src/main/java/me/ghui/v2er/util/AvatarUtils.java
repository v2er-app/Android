package me.ghui.v2er.util;

import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.Constants;

/**
 * Created by ghui on 23/06/2017.
 */

public class AvatarUtils {
    public static String adjustAvatar(String avatar) {
        if (PreConditions.isEmpty(avatar)) return null;
        //1.
        if (!avatar.startsWith(Constants.HTTPS_SCHEME) && !avatar.startsWith(Constants.HTTP_SCHEME)) {
            avatar = Constants.HTTPS_SCHEME + avatar;
        }

        //2.
        if (avatar.contains("_normal.png")) {
            avatar = avatar.replace("_normal.png", "_large.png");
        } else if (avatar.contains("_mini.png")) {
            avatar = avatar.replace("_mini.png", "_large.png");
        }

        //3. del param
//        if (avatar.contains("?")) {
//            avatar = avatar.substring(0, avatar.indexOf("?"));
//        }
        return avatar;
    }
}
