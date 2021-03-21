package me.ghui.v2er.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import androidx.browser.customtabs.CustomTabsIntent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.util.List;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.ImgFileProvider;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.UrlInterceptor;
import me.ghui.v2er.network.bean.UserInfo;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by ghui on 01/04/2017.
 */

public class Utils {
    public static int listSize(List list) {
        return list == null ? 0 : list.size();
    }

    public static String KEY(String key) {
        return Constants.PACKAGE_NAME + "_" + key;
    }

    public static CharSequence highlight(String text, boolean bold, int[]... hightIndexs) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        int color = App.get().getResources().getColor(R.color.colorAccent);
        for (int[] indexs : hightIndexs) {
            builder.setSpan(new ForegroundColorSpan(color), indexs[0], indexs[1], Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if (bold)
                builder.setSpan(new StyleSpan(Typeface.BOLD), indexs[0], indexs[1], Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public static void toggleKeyboard(boolean show, EditText inputEdit) {
        InputMethodManager imm = (InputMethodManager)
                App.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            inputEdit.requestFocus();
        } else {
            imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
        }
    }

    public static void openStorePage() {
        final String pkgName = App.get().getPackageName();
        openStorePage(pkgName);
    }

    public static void openStorePage(String pkgName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            App.get().startActivity(intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkgName));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            App.get().startActivity(intent);
        }
    }

    public static int getStatusBarHeight() {
        int statusBarHeight;
        int resourceId = App.get().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = App.get().getResources().getDimensionPixelSize(resourceId);
        } else {
            statusBarHeight = ScaleUtils.dp(24);
        }
        return statusBarHeight;
    }

    public static int getNavigationBarHeight() {
        // TODO: 2020-01-18
        // 1. 判断手机上是否有NavigationBar个别手机上会出错  -> 遮盖
        // 2. 个别手机上返回的NavigationBar高度有误   -> 导致Padding设置有误
        if (!hasNavBar(App.get().getResources())) return 0;
        int navigationBarHeight;
        int resourceId = App.get().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = App.get().getResources().getDimensionPixelSize(resourceId);
        } else {
            // 16dp, 48dp
            navigationBarHeight = ScaleUtils.dp(48);
        }
        return navigationBarHeight;
    }

    public static boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    public static boolean hasNavBar(Resources resources) {
        if (isEmulator()) return true;
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return (id > 0 && resources.getBoolean(id) || Pref.readBool(R.string.pref_key_title_btn_overlay));
    }

    public static void setPaddingForNavbar(View view) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getNavigationBarHeight());
    }

    public static void setPaddingForStatusBar(View view) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(), view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void copyToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Reply Text", text);
        clipboard.setPrimaryClip(clip);
    }

    public static boolean isAppInstalled(String... packageNames) {
        PackageManager packageManager = App.get().getPackageManager();
        if (packageManager == null)
            return false;
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList) {
            for (String packageName : packageNames) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }

    public static void sendMigrateMail(Activity context) {
        String content = null;
        if (UserUtils.getUserInfo() != null) {
            content = "V2EX用户名：" + UserUtils.getUserName() + "\n";
        }
        content += "请输入您的Google play账户：\n";
        sendEmail(context, context.getString(R.string.feedback_email),
                "获取内购兑换码",
                content);
    }

    public static void sendOfficalV2erEmail(Activity context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n\nPhoneInfo: ")
                .append("{")
                .append('\n')
                .append(" Release: ")
                .append(Build.VERSION.RELEASE)
                .append('\n')
                .append(" ,API Level: ")
                .append(Build.VERSION.SDK_INT)
                .append('\n')
                .append(" ,Brand: ")
                .append(Build.BRAND)
                .append('\n')
                .append(" ,Model: ")
                .append(Build.MODEL)
                .append('\n')
                .append(" ,AppVersion: ")
                .append(getVersionName())
                .append('\n');
        DisplayMetrics dm = ScaleUtils.getDisplayMetrics(context);
        String screen = " ( " + dm.widthPixels + ", " + dm.heightPixels + " ) ";
        sb.append(" ,Screen: ")
                .append(screen)
                .append('\n')
                .append(" ,Dpi: ")
                .append(dm.densityDpi)
                .append('\n')
                .append('}');
        UserInfo userInfo = UserUtils.getUserInfo();
        if (userInfo != null) {
            sb.append(" UserInfo: ")
                    .append("{ ")
                    .append('\n')
                    .append(" name: " + userInfo.getUserName())
                    .append('\n')
                    .append("} ");
        }
        String title = UserUtils.isPro() ? "【From V2er Pro】" : "【From V2er】";
        sendEmail(context, context.getString(R.string.feedback_email), title, sb.toString());
    }

    public static void sendEmail(Context context, String mail, String subject) {
        sendEmail(context, mail, subject, null);
    }

    public static void sendEmail(Context context, String mail, String subject, String content) {
        if (Check.isEmpty(mail)) return;
        String mailUri = mail;
        if (!mail.startsWith("mailto:")) {
            mailUri = "mailto:" + mail;
        }
        try {
            Intent request = new Intent(Intent.ACTION_VIEW);
            request.setData(Uri.parse(mailUri));
            request.putExtra(Intent.EXTRA_SUBJECT, subject);
            request.putExtra(Intent.EXTRA_TEXT, content);
            context.startActivity(request);
        } catch (ActivityNotFoundException e) {
            Voast.show("There are no email clients installed.");
        }
    }

    public static void jumpToJikeProfileInfo(Context context) {
        boolean hasJikeClient = Utils.isAppInstalled("com.ruguoapp.jike");
        if (hasJikeClient) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("jike://page.jk/user/862E2527-9EBC-4CED-A431-F5971343307D"));
            App.get().startActivity(intent);
        } else {
            Utils.openWap("https://h5.okjike.com/partner-fe/?from=singlemessage&isappinstalled=0#/share?id=862E2527-9EBC-4CED-A431-F5971343307D&shareType=invite&jk=e7fd66b5", context);
        }
    }

    // 跳转至微博个人页
    public static void jumpToWeiboProfileInfo(Context context) {
        boolean hasWeiboClient = Utils.isAppInstalled("com.weico.international",
                "com.hengye.share", "com.sina.weibo");
        if (hasWeiboClient) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("sinaweibo://userinfo?uid=3692784380"));
            App.get().startActivity(intent);
        } else {
            Utils.openWap("http://weibo.com/ghuiii", context);
        }
    }

    public static void jumpToTwitterProfilePage(Context context) {
        if (Utils.isAppInstalled("com.twitter.android")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("twitter://user?screen_name=sethcreate"));
            context.startActivity(intent);
        } else {
            Utils.openWap("https://mobile.twitter.com/sethcreate", context);
        }
    }

    public static void openWap(String url, Context context) {
        UrlInterceptor.openWapPage(url, context);
    }

    public static void openWap(String url, Context context, boolean forchOpenInWebView) {
        UrlInterceptor.openWapPage(url, context, forchOpenInWebView);
    }

    public static void openInBrowser(String url, Context context) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setToolbarColor(context.getColor(
                        DayNightUtil.isNightMode() ? R.color.colorPrimary_night : R.color.colorPrimary))
                .enableUrlBarHiding()
                .setShowTitle(true)
                .addDefaultShareMenuItem()
                .setStartAnimations(context, R.anim.open_enter_slide, R.anim.open_exit_slide)
                .setExitAnimations(context, R.anim.close_enter_slide, R.anim.close_exit_slide)
                .build();
        customTabsIntent.intent.setSelector(PkgUtils.getBrowserIntentExcludeV2er());
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /**
     * get App versionCode
     *
     * @return
     */
    public static String getVersionCode() {
        Context context = App.get();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * get App versionName
     *
     * @return
     */
    public static String getVersionName() {
        Context context = App.get();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    public static String[] cutString(String text, int cutPos) {
        if (Check.isEmpty(text)) return null;
        String[] result = new String[2];
        result[0] = text.substring(0, cutPos);
        result[1] = text.substring(cutPos, text.length());
        return result;
    }

    public static int getIntFromString(String textContainInt) {
        try {
            return Integer.parseInt(textContainInt.replaceAll("[\\D]", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static void openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }

    public static boolean isAppAvailable(String appName) {
        if (Check.isEmpty(appName)) return true;
        PackageManager pm = App.get().getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Drawable scaleImage(Drawable image, float scaleFactor) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
        image = new BitmapDrawable(App.get().getResources(), bitmapResized);
        return image;
    }

    public static void shareImg(File imgFile, String imageType, Context context) {
        if (Check.isEmpty(imageType)) return;
        Uri uri = ImgFileProvider.getUriForFile(context, context.getString(R.string.glide_img_provider), imgFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Shared image");
        intent.setType("image/" + imageType);
//        intent.putExtra(Intent.EXTRA_TEXT, "Look what I found!");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "Share image"));
    }

    public static String getTypeFromImgUrl(String url) {
        // TODO: 2019/1/4 http://sss.com/fds/1.png-sm
        String type = "*";
        if (Check.isEmpty(url)) return type;
        if (url.endsWith(".gif")) type = "gif";
        else if (url.endsWith(".png")) type = "png";
        else if (url.endsWith(".jpg")) type = "jpg";
        else if (url.endsWith(".jpeg")) type = "jpeg";
        else if (url.endsWith(".svg")) type = "svg";
        return type;
    }

    public static boolean isSVG(String url) {
        return "svg".equals(getTypeFromImgUrl(url));
    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static void copy2Clipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) App.get().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
    }

    public static void fullScreen(Window window, boolean fullScreen) {
        if (fullScreen) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    & ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    & ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    & ~View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public static String generateTopicLinkById(String topicId) {
        return "https://www.v2ex.com/t/" + topicId;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) App.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String extractDigits(String src) {
        if (TextUtils.isEmpty(src)) return "";
        return src.replaceAll("\\D+","");
    }

}

