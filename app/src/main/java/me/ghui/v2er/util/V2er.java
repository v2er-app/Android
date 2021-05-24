package me.ghui.v2er.util;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

/**
 * Analyse to wrapper V2er
 */
public class V2er {

    public static void capture(Throwable e) {
        Crashes.trackError(e);
    }

    public static void capture(String message) {
        capture(new Exception(message));
    }

    public static void logEvent(String msg) {
        Analytics.trackEvent(msg);
    }

    public static void setUserId (String userId) {
        if (Check.isEmpty(userId)) {
            capture("userId is empty, installedId: " + AppCenter.getInstallId());
            userId = AppCenter.getInstallId().get().toString();
        }
        AppCenter.setUserId(userId);
    }

}
