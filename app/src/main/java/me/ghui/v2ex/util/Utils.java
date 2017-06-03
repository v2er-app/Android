package me.ghui.v2ex.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

import me.ghui.v2ex.R;
import me.ghui.v2ex.general.App;
import me.ghui.v2ex.network.Constants;

/**
 * Created by ghui on 01/04/2017.
 */

public class Utils {
    public static int listSize(List list) {
        return list == null ? 0 : list.size();
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence text) {
        return !isEmpty(text);
    }

    public static boolean notEmpty(CharSequence text) {
        return !isEmpty(text);
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
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


}
