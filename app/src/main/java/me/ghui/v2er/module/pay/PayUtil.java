package me.ghui.v2er.module.pay;

import android.text.TextUtils;

import com.parse.ParseQuery;

import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.PayResultEvent;
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
            assert parseOrders.size() == 1;
            ParseOrder parseOrder = parseOrders.get(0);
            String returnedUserName = parseOrder.getUserName();
            boolean isPro;
            if (Check.notEmpty(returnedUserName) && returnedUserName.equals(UserUtils.getUserName())) {
                isPro = true;
            } else {
                // TODO: 2021/5/19
                isPro = false;
                String msg = "failed...";
            }

            L.e("userName: " + userName + " isPro: " + isPro);
            UserUtils.saveIsWechatPro(isPro);
            if (callback != null) {
                callback.onResult(isPro);
            }
        });
    }

    public static void checkIsGooglePro(boolean forceCheck) {
        BillingManager.get().checkIsGoogleProAsyc(forceCheck);
    }

}
