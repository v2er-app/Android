package me.ghui.v2er.util;


import android.support.annotation.IntDef;

import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 23/11/2017.
 */

public class DayNightUtil {

    private static final String KEY_DAY_NIGHT_MODEL = "day_night_mode_key";
    public static final int AUTO_MODE = 0;
    public static final int DAY_MODE = 1;
    public static final int NIGHT_MODE = 2;

    @IntDef({AUTO_MODE, DAY_MODE, NIGHT_MODE})
    public @interface DayNightMode{}

    @DayNightMode
    public static int getMode() {
        return Pref.readInt(KEY_DAY_NIGHT_MODEL, DAY_MODE);
    }

    public static boolean isNightMode(){
        return getMode() == NIGHT_MODE;
    }

    public static void saveMode(@DayNightMode int mode){
        Pref.save(KEY_DAY_NIGHT_MODEL, mode);
    }

}
