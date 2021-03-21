package me.ghui.v2er.module.pay;

import android.text.TextUtils;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.ghui.v2er.util.UserUtils;

import static com.parse.ParseException.USERNAME_MISSING;

public class PayUtil {
    public static String MCH_ID = "1607347568";
    private static String PARTNER_KEY = "7B5B0075D09C4FFC81EB9034B2341ED6";

    public static String createSign(Map<String, Object> params) {
        params.remove("sign");
        String stringA = packageSign(params, false);
        String stringSignTemp = stringA + "&key=" + PARTNER_KEY;
        return md5(stringSignTemp);
    }

    private static String packageSign(Map<String, Object> params, boolean urlEncoder) {
        TreeMap<String, Object> sortedParams = new TreeMap(params);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator var5 = sortedParams.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry)var5.next();
            String value = String.valueOf(param.getValue());
            if (!TextUtils.isEmpty(value)) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }

                sb.append(param.getKey()).append("=");
                if (urlEncoder) {
                    try {
                        value = urlEncode(value);
                    } catch (UnsupportedEncodingException var9) {
                    }
                }

                sb.append(value);
            }
        }

        return sb.toString();
    }

    public static String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "utf-8");
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String UUID() {
       return UUID.randomUUID()
               .toString()
               .replace("-", "")
               .toUpperCase();
    }

    public static String generateOrderId() {
        return UUID();
    }

    public static void checkIsPro(FindCallback<ParseOrder> callback) {
        String userName = UserUtils.getUserName();
        if (TextUtils.isEmpty(userName)) {
            if (callback != null) {
                callback.done(null,
                        new ParseException(USERNAME_MISSING, "No query result, since userName is null"));
            }
        }
        ParseQuery<ParseOrder> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("V2EXUserName", userName);
        query.findInBackground(callback);
    }


}
