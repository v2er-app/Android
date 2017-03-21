package me.ghui.v2ex.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import me.ghui.v2ex.general.App;

/**
 * Created by ghui on 21/03/2017.
 */

public class ResUtils {
	public static int getColor(@ColorRes int colorId) {
		return ContextCompat.getColor(App.getInstance(), colorId);
	}

	public static int getColor(@ColorRes int colorId, Context context) {
		return ContextCompat.getColor(context, colorId);
	}
}
