package me.ghui.v2ex.util;

import android.content.Context;

import me.ghui.v2ex.general.App;

/**
 * Created by ghui on 8/4/15.
 */
public class ScaleUtils {

    public static float getScreenW() {
        return App.get().getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenContentH() {
        return App.get().getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScaleRate(Context context) {
        int sw = context.getResources().getDisplayMetrics().widthPixels;
        int sh = context.getResources().getDisplayMetrics().heightPixels;
        return Math.min(sw / 640f, sh / 1136f);
    }

    public static int dp(float value) {
        return (int) (App.get().getResources().getDisplayMetrics().density * value + 0.5);
    }

    public static int dp(float value, Context context) {
        return (int) (context.getResources().getDisplayMetrics().density * value + 0.5);
    }

    /**
     * 得到dp的字面值
     * eg: 2dp, return 2;
     *
     * @param pix
     * @return
     */
    public static float px2Dp(float pix) {
        return pix / App.get().getResources().getDisplayMetrics().density;
    }

    public static float ScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
