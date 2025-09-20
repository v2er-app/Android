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
            case "特大":
                id = R.dimen.extralargeTextSize;
                break;
            case "中":
            default:
                id = R.dimen.mediumTextSize;
        }
        return App.get().getResources().getDimension(id);
    }

    public static float getTitleSize() {
        String size = Pref.read(R.string.pref_key_fontsize);
        int id;
        switch (size) {
            case "小":
                id = R.dimen.smallTextSize;
                break;
            case "大":
                id = R.dimen.largeTextSize;
                break;
            case "特大":
                id = R.dimen.extralargeTextSize;
                break;
            case "中":
            default:
                id = R.dimen.mediumTextSize;
        }
        return App.get().getResources().getDimension(id);
    }

    public static float getSubTextSize() {
        String size = Pref.read(R.string.pref_key_fontsize);
        int id;
        switch (size) {
            case "小":
                id = R.dimen.microTextSize;
                break;
            case "大":
                id = R.dimen.mediumTextSize;
                break;
            case "特大":
                id = R.dimen.largeTextSize;
                break;
            case "中":
            default:
                id = R.dimen.smallTextSize;
        }
        return App.get().getResources().getDimension(id);
    }

    public static float getHtmlFontSize() {
        return ScaleUtils.px2Dp(getContentSize());
    }

    /**
     * Get scaling ratio based on font size preference
     * This ratio can be applied to any text view to scale it proportionally
     * @return scaling ratio (1.0 for medium/default, higher for larger sizes)
     */
    public static float getScalingRatio() {
        String size = Pref.read(R.string.pref_key_fontsize);
        switch (size) {
            case "小":
                return 0.875f;  // 87.5% of default
            case "大":
                return 1.25f;   // 125% of default
            case "特大":
                return 1.5f;    // 150% of default
            case "中":
            default:
                return 1.0f;    // 100% default
        }
    }

    /**
     * Apply scaling to a given font size in pixels
     * @param originalSize the original font size in pixels
     * @return scaled font size in pixels
     */
    public static float getScaledSize(float originalSize) {
        return originalSize * getScalingRatio();
    }

}
