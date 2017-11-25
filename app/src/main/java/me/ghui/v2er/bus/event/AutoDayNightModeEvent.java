package me.ghui.v2er.bus.event;

/**
 * Created by ghui on 24/11/2017.
 */

/**
 * preference 日夜间模式切换
 */
public class AutoDayNightModeEvent {
    public boolean isAuto;

    public AutoDayNightModeEvent(boolean isAuto) {
        this.isAuto = isAuto;
    }
}
