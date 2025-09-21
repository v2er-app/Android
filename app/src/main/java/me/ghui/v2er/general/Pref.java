package me.ghui.v2er.general;

import androidx.annotation.StringRes;

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

    public static int readInt(String key) {
        return Prefs.with(app()).readInt(key);
    }

    public static int readInt(String key, int defaultValue) {
        return Prefs.with(app()).readInt(key, defaultValue);
    }

    public static int readInt(@StringRes int key, int defaultValue) {
        return readInt(app().getString(key), defaultValue);
    }

    public static boolean readBool(@StringRes int key) {
        return readBool(app().getString(key));
    }

    public static boolean readBool(@StringRes int key, boolean defaultValue) {
        return readBool(app().getString(key), defaultValue);
    }

    public static boolean readBool(String key) {
        return Prefs.with(app()).readBoolean(key);
    }

    public static boolean readBool(String key, boolean defaultValue) {
        return Prefs.with(app()).readBoolean(key, defaultValue);
    }

    public static void saveBool(String key, boolean value) {
        Prefs.with(app()).writeBoolean(key, value);
    }

    public static void saveBool(@StringRes int key, boolean value) {
        Prefs.with(app()).writeBoolean(app().getString(key), value);
    }

    public static void save(@StringRes int key, String value) {
        Prefs.with(app()).write(app().getString(key), value);
    }

    public static void save(String key, String value) {
        Prefs.with(app()).write(key, value);
    }

    public static void save(String key, int value) {
        Prefs.with(app()).writeInt(key, value);
    }

    public static void save(@StringRes int key, int value) {
        Prefs.with(app()).writeInt(app().getString(key), value);
    }

    public static void save(String key, boolean value) {
        Prefs.with(app()).writeBoolean(key, value);
    }

}
