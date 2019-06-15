package me.ghui.v2er.util;


import android.support.annotation.IntDef;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.ghui.v2er.R;
import me.ghui.v2er.bus.event.DayNightModeEvent;
import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 23/11/2017.
 */

public class DayNightUtil {
    public static final int DAY_MODE = 1;
    public static final int NIGHT_MODE = 2;
    private static final String KEY_DAY_NIGHT_MODEL = "day_night_mode_key";

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private static int lasMode = getMode();

    public static boolean isAutoModeEnabled() {
        return Pref.readBool(R.string.pref_key_auto_daynight_switch, false);
    }

    @DayNightMode
    public static int getMode() {
        int currentMode = Pref.readInt(KEY_DAY_NIGHT_MODEL, DAY_MODE);
        if (isAutoModeEnabled()) {
            String dayStartTimeStr = Pref.read(R.string.pref_key_day_mode_start_time);
            String nightStartTimeStr = Pref.read(R.string.pref_key_night_mode_start_time);
            String currentTimeStr = sdf.format(new Date());
            long dayStartTimemills;
            long nightStartTimemills;
            long currentTimemills;
            try {
                dayStartTimemills = sdf.parse(dayStartTimeStr).getTime();
                nightStartTimemills = sdf.parse(nightStartTimeStr).getTime();
                currentTimemills = sdf.parse(currentTimeStr).getTime();
                if (currentTimemills >= dayStartTimemills && currentTimemills < nightStartTimemills) {
                    currentMode = DAY_MODE;
                } else {
                    currentMode = NIGHT_MODE;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (currentMode != lasMode) {
            lasMode = currentMode;
            // 自动模式下发生模式切换需要发送模式变更事件，通知别的页面做同步改变
            EventBus.getDefault().post(DayNightModeEvent.get(currentMode));
        }
        return currentMode;
    }

    public static boolean isNightMode() {
        return getMode() == NIGHT_MODE;
    }

    public static void saveMode(@DayNightMode int mode) {
        Pref.save(KEY_DAY_NIGHT_MODEL, mode);
        // todo send change broadcast
    }

    public static void saveAutoMode(boolean auto) {
        Pref.saveBool(R.string.pref_key_auto_daynight_switch, auto);
    }

    @IntDef({DAY_MODE, NIGHT_MODE})
    public @interface DayNightMode {
    }


    // 1. 当自动切换夜间模式打开时，发送切换的event给所有订阅者，所有的订阅者检查是否需要切换自身模式
    // 2. 每打开一个页面时，需要检查一下是否需要自动切换模式，若需要则发送一个自动切换的事件给所有订阅者

}
