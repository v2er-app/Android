package me.ghui.v2er.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.network.Constants;

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
        // TODO: 24/05/2017 builder 
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

    public static void toast(String msg) {
        toast(msg, false);
    }

    public static void toast(String msg, boolean isToastLong) {
        Toast.makeText(App.get(), msg, isToastLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void openStorePage() {
        final String appPackageName = App.get().getPackageName();
//        final String appPackageName = "com.czbix.v2ex";
        try {
            App.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            App.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
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
        int navigationBarHeight = 0;
        int resourceId = App.get().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = App.get().getResources().getDimensionPixelSize(resourceId);
        } else {
            navigationBarHeight = ScaleUtils.dp(48);
        }
        return navigationBarHeight;
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

    public static void transparentBars(Window window, int statusBarColor, int navBarColor) {
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);
        window.setNavigationBarColor(navBarColor);
    }

//    public static void transparentStatusBar(Window window, int barColor) {
//        window.requestFeature(Window.FEATURE_NO_TITLE);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(barColor);
//    }
//
//    public static void transparentNavBar(Window window, int barColor) {
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setNavigationBarColor(barColor);
//    }

    public static void sendEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.feedback_email)});
//        intent.putExtra(Intent.EXTRA_SUBJECT, "From V2er");
//        intent.putExtra(Intent.EXTRA_TEXT, "Body");
        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            toast("There are no email clients installed.");
        }
    }

}
