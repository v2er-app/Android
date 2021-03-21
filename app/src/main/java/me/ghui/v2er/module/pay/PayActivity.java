package me.ghui.v2er.module.pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.general.WapActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

public class PayActivity extends WapActivity {
    private static String ORDER_ID_KEY = KEY("order.id.key");
    private String mOrderId;

    public static void startPay(String h5PayUrl, String orderId, Context context) {
        Navigator.from(context)
                .putExtra(URL_KEY, h5PayUrl)
                .putExtra(ORDER_ID_KEY, orderId)
                .to(PayActivity.class)
                .start();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void configWebView(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void parseExtras(Intent intent) {
        super.parseExtras(intent);
        mOrderId = intent.getStringExtra(ORDER_ID_KEY);
    }

    @Override
    protected void firstLoad(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Referer", "https://lessmore.io");
        super.firstLoad(headers);
    }

    @Override
    protected boolean checkIntercept(String url) {
        if (TextUtils.isEmpty(url)) return false;
        // Case 1: intercept to open wechat pay
        if (url.startsWith("weixin://")) {
            if (!Utils.isAppInstalled("com.tencent.mm")) {
                return false;
            }
            // trigger wechat client
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }
        // Case 2: intercept to check pay result
        if (url.startsWith("https://lessmore.io/pay/callback")) {
            L.d("Pay result : " + url);
            // TODO() Check from own server
//            PayUtil.checkIsPro();
            // TODO: check whether the pay is success.
            Voast.debug("Check pay result...");
            Map<String, Object> params = new HashMap<>(2);
            params.put("out_trade_no", mOrderId);
            params.put("mch_id", PayUtil.MCH_ID);
            String sign = PayUtil.createSign(params);

            delay(1000, () -> APIService.get().fetchOrderStatus(mOrderId, PayUtil.MCH_ID, sign)
                    .compose(rx())
                    .subscribe(new GeneralConsumer<OrderStatusInfo>() {
                        @Override
                        public void onConsume(OrderStatusInfo orderStatusInfo) {
                            L.d("PayStatusInfo: " + orderStatusInfo);
                            Voast.debug(orderStatusInfo.paid() ? "Pay successfully" : "Pay failed");
//                            if (!orderStatusInfo.paid()) return;
//                            ParseOrder parseOrder = orderStatusInfo.toParseOrder();
//                            if (parseOrder == null) return;
//                            parseOrder.saveInBackground(e -> {
//                                if (e != null && e.getCode() != 0) {
//                                    // TODO: 如果写入db失败怎么处理？？？
//                                    // Failed, try again.
//                                    parseOrder.saveEventually();
//                                }
//                            });
                        }
                    }));
            return true;
        }
        return false;
    }

    @Override
    protected void onWapPageStarted(WebView webview, String url) {
        super.onWapPageStarted(webview, url);
    }

    @Override
    protected void onWapPageFinished(WebView webview, String url) {
        super.onWapPageFinished(webview, url);
    }

}
