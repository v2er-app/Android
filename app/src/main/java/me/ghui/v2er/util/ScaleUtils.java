package me.ghui.v2er.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import me.ghui.v2er.general.App;

/**
 * Created by ghui on 8/4/15.
 */
public class ScaleUtils {

    public static int getScreenW(Activity activity) {
        return getScreenSize(activity)[0];
    }

    public static int getScreenH(Activity activity) {
        return getScreenSize(activity)[1];
    }

    public static int getScreenContentH() {
        return App.get().getResources().getDisplayMetrics().heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        return dm;
    }

    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics dm = getDisplayMetrics(activity);
        return new int[]{dm.widthPixels, dm.heightPixels};
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
