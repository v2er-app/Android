package me.ghui.v2er.bus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ghui on 24/11/2017.
 */

public class Bus {

    public static void register(Object target) {
        EventBus.getDefault().register(target);
    }

    public static void unRegister(Object target) {
        EventBus.getDefault().unregister(target);
    }

    public static void post(Object message) {
        EventBus.getDefault().post(message);
    }
}
