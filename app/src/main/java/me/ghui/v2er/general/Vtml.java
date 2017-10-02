package me.ghui.v2er.general;

import android.text.Html;
import android.text.SpannableStringBuilder;

/**
 * Created by ghui on 30/08/2017.
 */

public class Vtml {

    public static CharSequence fromHtml(String html) {
        if (PreConditions.isEmpty(html)) return null;
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(html);
        CharSequence content = Vtml.removePadding(spanned);
        return content;
    }

    public static CharSequence removePadding(SpannableStringBuilder text) {
        if (PreConditions.isEmpty(text)) return null;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.delete(text.length() - 1, text.length());
        }
        return text;
    }
}
