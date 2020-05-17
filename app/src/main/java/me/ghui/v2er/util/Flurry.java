package me.ghui.v2er.util;

import com.flurry.android.FlurryAgent;

/**
 * Flurry to wrapper FlurryClient
 */
public class Flurry {
    public static void capture(String message, Throwable e) {
        FlurryAgent.onError("0", message, e);
    }

    public static void capture(String message) {
        capture(message, new Throwable(message));
    }

    public static void capture(Throwable e) {
        capture("", e);
    }
}
