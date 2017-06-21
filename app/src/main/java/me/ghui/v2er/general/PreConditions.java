package me.ghui.v2er.general;

import java.util.List;

/**
 * Created by ghui on 12/06/2017.
 */

public class PreConditions {
    public static boolean isEmpty(CharSequence... texts) {
        for (int i = 0; i < texts.length; i++) {
            CharSequence text = texts[i];
            if (text == null || text.length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean notEmpty(CharSequence... text) {
        return !isEmpty(text);
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }
}
