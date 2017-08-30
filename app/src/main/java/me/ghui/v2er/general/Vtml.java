package me.ghui.v2er.general;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by ghui on 30/08/2017.
 */

public class Vtml {

    public static Spanned fromHtml(String html) {
        if (PreConditions.isEmpty(html)) return null;
        return Html.fromHtml(html);
    }
}
