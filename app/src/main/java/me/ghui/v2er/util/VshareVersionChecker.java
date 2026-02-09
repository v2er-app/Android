package me.ghui.v2er.util;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2er.network.APIService;

/**
 * Checks for vshare page version updates
 * Queries the API every 24 hours to check if the vshare page has been updated
 */
public class VshareVersionChecker {
    private static final String PREFS_NAME = "vshare_version";
    private static final String KEY_LAST_VERSION = "last_version";
    private static final String KEY_SERVER_VERSION = "server_version";
    private static final String KEY_LAST_CHECK_TIME = "last_check_time";
    private static final long CHECK_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    private final Context context;
    private final SharedPreferences prefs;

    public VshareVersionChecker(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Checks if there's a new version available
     * Only performs network request if 24 hours have passed since last check
     *
     * @return Observable<Boolean> - true if there's an update, false otherwise
     */
    public Observable<Boolean> checkForUpdate() {
        long lastCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0);
        long currentTime = System.currentTimeMillis();

        // If less than 24 hours since last check, return cached result
        if (currentTime - lastCheckTime < CHECK_INTERVAL) {
            return Observable.just(hasUpdate());
        }

        // Perform network request
        return APIService.get()
                .getVshareVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(versionInfo -> {
                    if (versionInfo != null && versionInfo.isValid()) {
                        int serverVersion = versionInfo.getVersion();
                        int localVersion = prefs.getInt(KEY_LAST_VERSION, 0);

                        // Update last check time and server version
                        prefs.edit()
                                .putLong(KEY_LAST_CHECK_TIME, currentTime)
                                .putInt(KEY_SERVER_VERSION, serverVersion)
                                .apply();

                        // If server version is newer, return true
                        return serverVersion > localVersion;
                    }
                    return false;
                })
                .onErrorReturn(throwable -> {
                    // On error, return cached result
                    return hasUpdate();
                });
    }

    /**
     * Marks the current version as viewed by the user
     * This should be called when the user clicks on the vshare menu item
     */
    public void markAsViewed() {
        int serverVersion = prefs.getInt(KEY_SERVER_VERSION, 0);
        if (serverVersion > 0) {
            prefs.edit()
                    .putInt(KEY_LAST_VERSION, serverVersion)
                    .apply();
        }
    }

    /**
     * Checks if there's an update based on cached data
     * Does not perform network request
     *
     * @return true if there's an update, false otherwise
     */
    private boolean hasUpdate() {
        int serverVersion = prefs.getInt(KEY_SERVER_VERSION, 0);
        int lastViewedVersion = prefs.getInt(KEY_LAST_VERSION, 0);
        return serverVersion > lastViewedVersion;
    }

    /**
     * Forces a check for updates regardless of the 24-hour interval
     *
     * @return Observable<Boolean> - true if there's an update, false otherwise
     */
    public Observable<Boolean> forceCheckForUpdate() {
        // Reset last check time to force an update
        prefs.edit()
                .putLong(KEY_LAST_CHECK_TIME, 0)
                .apply();

        return checkForUpdate();
    }
}
