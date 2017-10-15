package me.ghui.v2er.general;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Voast;

/**
 * Created by ghui on 12/06/2017.
 */

public class PreConditions {
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean notEmpty(CharSequence... texts) {
        for (CharSequence text : texts) {
            if (isEmpty(text)) return false;
        }
        return true;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }


    public static boolean notLoginAndProcessToLogin(boolean finishCurrentPage, Context context) {
        if (!UserUtils.isLogin()) {
            Voast.show("登录后才能进行此操作");
            Navigator.from(context).to(LoginActivity.class).start();
            if (context instanceof Activity && finishCurrentPage) {
                ((Activity) context).finish();
            }
            return true;
        }
        return false;
    }
}
