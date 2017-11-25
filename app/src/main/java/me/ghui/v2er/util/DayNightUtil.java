package me.ghui.v2er.util;


import android.support.v7.app.AppCompatDelegate;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 23/11/2017.
 */

public class DayNightUtil {

    public static final String KEY_DAY_NIGHT_MODEL = "day_night_mode_key";

    public static void setUIMode(@AppCompatDelegate.NightMode int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static void initUIMode() {
        int mode;
        if (isAutoSwitch()) mode = AppCompatDelegate.MODE_NIGHT_AUTO;
        else {
            mode = Pref.readInt(KEY_DAY_NIGHT_MODEL, AppCompatDelegate.MODE_NIGHT_NO);
        }
        setUIMode(mode);
    }

    public static boolean isAutoSwitch() {
        return Pref.readBool(App.get().getString(R.string.pref_key_auto_daynight));
    }

    public static int getMode() {
        return Pref.readInt(KEY_DAY_NIGHT_MODEL, AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static boolean isNightModeOn() {
        return getMode() == AppCompatDelegate.MODE_NIGHT_YES || isAutoSwitch();
    }

}
