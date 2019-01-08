package me.ghui.v2er.util;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Pref;

public class FontSizeUtil {

    public static float getContentSize() {
        String size = Pref.read(R.string.pref_key_fontsize);
        int id;
        switch (size) {
            case "小":
                id = R.dimen.smallTextSize;
                break;
            case "大":
                id = R.dimen.largeTextSize;
                break;
            case "中":
            default:
                id = R.dimen.mediumTextSize;
        }
        return App.get().getResources().getDimension(id);
    }

    public static float getHtmlFontSize() {
        return ScaleUtils.px2Dp(getContentSize());
    }

}
