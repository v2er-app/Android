package me.ghui.v2er.util;


import android.content.res.Configuration;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.ghui.v2er.R;
import me.ghui.v2er.bus.event.DayNightModeEvent;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 23/11/2017.
 */

public class DarkModelUtils {
    public static final int DEFAULT_MODE = 1;
    public static final int DARK_MODE = 2;
    private static final String KEY_LIGHT_DARK_MODEL = "day_night_mode_key";
    public static final String FOLLOW_SYSTEM = "0";
    public static final String FOLLOW_TIME = "1";

    @StringDef({FOLLOW_SYSTEM, FOLLOW_TIME})
    public @interface AutoSwitchMode {
    }

    @IntDef({DEFAULT_MODE, DARK_MODE})
    public @interface DayNightMode {
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private static int lasMode = getMode();

    @DayNightMode
    public static int getMode() {
        int currentMode = Pref.readInt(KEY_LIGHT_DARK_MODEL, DEFAULT_MODE);
        if (isAutoModeEnabled()) {
            if (isAutoChangeBySystemEnabled()) {
                // Auto change follow system
                currentMode = isSystemInDarkMode() ? DARK_MODE : DEFAULT_MODE;
            } else {
                // Auto change by time
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
                        currentMode = DEFAULT_MODE;
                    } else {
                        currentMode = DARK_MODE;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (currentMode != lasMode) {
            lasMode = currentMode;
            // 自动模式下发生模式切换需要发送模式变更事件，通知别的页面做同步改变
            EventBus.getDefault().post(DayNightModeEvent.get(currentMode));
        }
        return currentMode;
    }

    public static boolean isDarkMode() {
        return getMode() == DARK_MODE;
    }

    public static boolean isAutoModeEnabled() {
        if (!UserUtils.isPro()) return false;
        return Pref.readBool(R.string.pref_key_auto_dark_mode_switch, false);
    }

    public static boolean isAutoChangeByTimeEnabled() {
        if (!isAutoModeEnabled()) return false;
        String currentAutoMode = Pref.read(App.get().getString(R.string.pref_key_auto_dark_mode_way));
        return FOLLOW_TIME.equals(currentAutoMode);
    }

    public static boolean isAutoChangeBySystemEnabled() {
        if (!isAutoModeEnabled()) return false;
        String currentAutoMode = Pref.read(App.get().getString(R.string.pref_key_auto_dark_mode_way));
        return FOLLOW_SYSTEM.equals(currentAutoMode);
    }

    public static boolean isSystemInDarkMode() {
        int nightModeFlags =
                App.get().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void saveModeMannually(@DayNightMode int mode) {
        Pref.save(KEY_LIGHT_DARK_MODEL, mode);
        // todo send change broadcast
    }

    public static void saveEnableAutoSwitch(boolean enable) {
        Pref.saveBool(R.string.pref_key_auto_dark_mode_switch, enable);
    }

    public static void saveAutoSwitchMode(@AutoSwitchMode String autoSwitchMode) {
        Pref.save(R.string.pref_key_auto_dark_mode_way, autoSwitchMode);
    }


    // 1. 当自动切换夜间模式打开时，发送切换的event给所有订阅者，所有的订阅者检查是否需要切换自身模式
    // 2. 每打开一个页面时，需要检查一下是否需要自动切换模式，若需要则发送一个自动切换的事件给所有订阅者

}
