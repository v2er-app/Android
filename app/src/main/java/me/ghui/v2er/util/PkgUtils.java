package me.ghui.v2er.util;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import io.reactivex.annotations.NonNull;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.general.App;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

/**
 * Created by ghui on 15/10/2017.
 */

public class PkgUtils {
    public static String getDefaultAppFor(@NonNull String uri) {
        if (Check.isEmpty(uri)) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        List<ResolveInfo> resolveInfos = App.get().getPackageManager().queryIntentActivities(intent, MATCH_DEFAULT_ONLY);
        //the size of resolveInfos will be 1 if you had set a default app, otherwise will large than 1;
        return resolveInfos.get(0).activityInfo.packageName;
    }

    public static String getDefaultBrowser() {
        return getDefaultAppFor("https://www.google.com/");
    }
}
