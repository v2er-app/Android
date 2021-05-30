package me.ghui.v2er.module.pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.PayResultEvent;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.general.WapActivity;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

public class WXPayActivity extends WapActivity {
    private static String ORDER_ID_KEY = KEY("order.id.key");
    private String mOrderId;

    public static void startPay(String h5PayUrl, String orderId, Context context) {
        Navigator.from(context)
                .putExtra(URL_KEY, h5PayUrl)
                .putExtra(ORDER_ID_KEY, orderId)
                .to(WXPayActivity.class)
                .start();
    }

    @Override
    protected void initTheme() {
        setTheme(R.style.TranslucentTheme);
        getWindow().getDecorView().setAlpha(0);
    }

    @Override
    protected void init() {
        super.init();
        mRootView.setAlpha(0);
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
            showLoading();
            delay(2 * 1000, () -> {
                PayUtil.checkIsWechatPro(isWechatPro -> {
                    hideLoading();
                    PayResultEvent payResultEvent = new PayResultEvent(isWechatPro,
                            PayResultEvent.PayWay.WECHAT_PAY, null);
                    Bus.post(payResultEvent);
                    finish();
                });
            });
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

    @Override
    protected void onWapReceivedTitle(String title) {
//        super.onWapReceivedTitle(title);
    }
}
