package me.ghui.v2er.module.pay;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Trace;
import android.text.TextUtils;

import com.parse.ParseQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.PayResultEvent;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.BillingManager;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;

public class PayUtil {

    public interface CheckWechatProInfoListener {
        void onResult(boolean isWechatPro);
    }

    public static void checkIsWechatPro() {
        checkIsWechatPro(null);
    }

    public static void checkIsWechatPro(CheckWechatProInfoListener callback) {
        if (!UserUtils.isLogin()) return;
        String userName = UserUtils.getUserName();
        if (TextUtils.isEmpty(userName)) {
            if (callback != null) {
                callback.onResult(false);
            }
        }
        ParseQuery<ParseOrder> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("v2exUserName", userName);
        query.findInBackground((parseOrders, e) -> {
            assert parseOrders.size() <= 1;
            boolean isPro;
            if (Check.isEmpty(parseOrders)) {
                isPro = false;
            } else {
                ParseOrder parseOrder = parseOrders.get(0);
                String returnedUserName = parseOrder.getUserName();
                if (Check.notEmpty(returnedUserName)
                        && returnedUserName.equals(UserUtils.getUserName())) {
                    isPro = true;
                } else {
                    isPro = false;
                }
            }

            L.e("userName: " + userName + " ,isWechatPro: " + isPro);
            UserUtils.saveIsWechatPro(isPro);
            if (callback != null) {
                callback.onResult(isPro);
            }
        });
    }

    public static void checkIsGooglePro(boolean forceCheck) {
        checkIsGooglePro(forceCheck, null);
    }

    public static void checkIsGooglePro(boolean forceCheck, BillingManager.CheckResultListener resultListener) {
        BillingManager.get().checkIsGoogleProAsyc(forceCheck, resultListener);
    }

    private static String getSign() {
        Context context = App.get();
        String result = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            result = encryptMD5(sign.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String encryptMD5(byte[] byteStr) {
        StringBuilder md5StrBuff = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (byte b : byteArray) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & b));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return md5StrBuff.toString();
    }

    public static String createSign(Map<String, Object> params) {
        String stringA = packageSign(params, false);
        String signKey = getSign();
        L.e("signKey: " + signKey);
        String stringSignTemp = stringA + "&key=" + signKey;
        return md5(stringSignTemp);
    }

    private static String packageSign(Map<String, Object> params, boolean urlEncoder) {
        TreeMap<String, Object> sortedParams = new TreeMap(params);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator var5 = sortedParams.entrySet().iterator();

        while (var5.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry) var5.next();
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

}
