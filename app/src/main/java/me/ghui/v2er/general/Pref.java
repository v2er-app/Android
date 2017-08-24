package me.ghui.v2er.general;

import android.support.annotation.StringRes;

import es.dmoral.prefs.Prefs;

/**
 * Created by ghui on 11/06/2017.
 */

public class Pref {

    private static App app() {
        return App.get();
    }

    public static String read(@StringRes int key) {
        return read(app().getString(key));
    }

    public static String read(String key) {
        return Prefs.with(app()).read(key);
    }

    public static boolean readBool(@StringRes int key) {
        return readBool(app().getString(key));
    }

    public static boolean readBool(String key) {
        return Prefs.with(app()).readBoolean(key);
    }

    public static void saveBool(String key, boolean value) {
        Prefs.with(app()).writeBoolean(key, value);
    }

    public static void save(String key, String value) {
        Prefs.with(app()).write(key, value);
    }
}
