package me.ghui.v2er.util;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Pref;

public class FontSizeUtil {

    /**
     * Common helper method to get font dimension based on size preference
     * @param smallDimen dimension resource for "小" size
     * @param mediumDimen dimension resource for "中" size (default)
     * @param largeDimen dimension resource for "大" size
     * @param extraLargeDimen dimension resource for "特大" size
     * @return font size in pixels
     */
    private static float getFontSizeForPreference(int smallDimen, int mediumDimen,
                                                   int largeDimen, int extraLargeDimen) {
        String size = Pref.read(R.string.pref_key_fontsize);
        int id;
        switch (size) {
            case "小":
                id = smallDimen;
                break;
            case "大":
                id = largeDimen;
                break;
            case "特大":
                id = extraLargeDimen;
                break;
            case "中":
            default:
                id = mediumDimen;
        }
        return App.get().getResources().getDimension(id);
    }

    public static float getContentSize() {
        return getFontSizeForPreference(
            R.dimen.smallTextSize,
            R.dimen.mediumTextSize,
            R.dimen.largeTextSize,
            R.dimen.extralargeTextSize
        );
    }

    public static float getTitleSize() {
        return getFontSizeForPreference(
            R.dimen.smallTextSize,
            R.dimen.mediumTextSize,
            R.dimen.largeTextSize,
            R.dimen.extralargeTextSize
        );
    }

    public static float getSubTextSize() {
        return getFontSizeForPreference(
            R.dimen.microTextSize,
            R.dimen.smallTextSize,
            R.dimen.mediumTextSize,
            R.dimen.largeTextSize
        );
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
