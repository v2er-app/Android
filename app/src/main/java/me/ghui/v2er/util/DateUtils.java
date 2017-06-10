package me.ghui.v2er.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ghui on 02/04/2017.
 */

public class DateUtils {

    public static String parseDate(long time) {
        return new SimpleDateFormat("HH:mm").format(new Date(time));
    }
}
