package me.ghui.v2er.util;

import android.widget.Toast;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.general.App;

/**
 * Created by ghui on 11/07/2017.
 */

public class Voast {
    public static void show(String msg) {
        if (Check.isEmpty(msg)) return;
        show(msg, false);
    }

    public static void show(String msg, boolean isToastLong) {
        Toast.makeText(App.get(), msg, isToastLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void debug(String msg) {
        if (BuildConfig.DEBUG) {
            show(msg);
        }
    }
}
