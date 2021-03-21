package me.ghui.v2er.util;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import me.ghui.v2er.general.App;

/**
 * Created by ghui on 21/03/2017.
 */

public class ResUtils {
    public static int getColor(@ColorRes int colorId) {
        return ContextCompat.getColor(App.get(), colorId);
    }

    public static int getColor(@ColorRes int colorId, Context context) {
        return ContextCompat.getColor(context, colorId);
    }
}
