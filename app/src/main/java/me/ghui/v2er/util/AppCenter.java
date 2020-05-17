package me.ghui.v2er.util;

import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

/**
 * Flurry to wrapper FlurryClient
 */
public class AppCenter {

    public static void capture(Throwable e) {
        Crashes.trackError(e);
    }

    public static void capture(String message) {
        capture(new Exception(message));
    }

    public static void logEvent(String msg) {
        Analytics.trackEvent(msg);
    }

}
