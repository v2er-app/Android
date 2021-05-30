package me.ghui.v2er.bus.event;

/**
 * Created by ghui on 24/11/2017.
 */

import me.ghui.v2er.util.DarkModelUtils;

/**
 * preference 日夜间模式切换
 */
public class DayNightModeEvent implements Cloneable {
    private int mode;

    public static DayNightModeEvent get(@DarkModelUtils.DayNightMode int mode) {
        return new DayNightModeEvent(mode);
    }

    private DayNightModeEvent(@DarkModelUtils.DayNightMode int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public String getModeName() {
        return mode == DarkModelUtils.DEFAULT_MODE ? "DayMode" : "NightMode";
    }

    public DayNightModeEvent copy() {
        return new DayNightModeEvent(mode);
    }

}
