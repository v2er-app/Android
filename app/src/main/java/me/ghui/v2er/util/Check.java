package me.ghui.v2er.util;

import java.util.List;

/**
 * Created by ghui on 30/10/2017.
 */

public class Check {
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean notEmpty(CharSequence... texts) {
        for (CharSequence text : texts) {
            if (isEmpty(text)) return false;
        }
        return true;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }
}
