package me.ghui.v2er.util;

import android.content.Context;
import androidx.annotation.AttrRes;
import android.util.TypedValue;

public class Theme {

    public static int getColor(@AttrRes int resId, Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        // attr对应非referance
        if(typedValue.resourceId == 0) return typedValue.data;
        return context.getResources().getColor(typedValue.resourceId);
    }
}
