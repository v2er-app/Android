package me.ghui.v2er.module.drawer.dailyhot;

import java.io.Serializable;

import me.ghui.v2er.network.bean.DailyHotInfo;

public class DailyHotInfoWrapper implements Serializable {
    DailyHotInfo dailyHotInfo;

    private DailyHotInfoWrapper(DailyHotInfo dailyHotInfo) {
        this.dailyHotInfo = dailyHotInfo;
    }

    static DailyHotInfoWrapper create(DailyHotInfo dailyHotInfo) {
        return new DailyHotInfoWrapper(dailyHotInfo);
    }
}
