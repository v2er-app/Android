package me.ghui.v2er.module.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebSettings;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.general.WapActivity;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.DailyInfo;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 13/08/2017.
 */

public class SignInWithGoogleActivity extends WapActivity {
    private static final String UA_GOOGLE_SIGN = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36";
    private static final String REFER_GOOGLE = Constants.BASE_URL + "/signin?next=/mission/daily";
    private static final String URL_GOOGLE_SIGNIN = Constants.BASE_URL + "/auth/google?once=";

    public static void open(Context context, String once) {
        String url = URL_GOOGLE_SIGNIN + once;
        Navigator.from(context)
                .putExtra(URL_KEY, url)
                .putExtra(FORCH_OPENED_WEBVIEW, true)
                .to(SignInWithGoogleActivity.class)
                .start();
    }

    @Override
    protected void configWebView(WebSettings webSettings) {
        super.configWebView(webSettings);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString(UA_GOOGLE_SIGN);
    }

    @Override
    protected void firstLoad(Map<String, String> headers) {
        headers = new HashMap<>();
        headers.put("Refer", REFER_GOOGLE);
        super.firstLoad(headers);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected boolean checkIntercept(String currentUrl) {
        Log.d("GoogleSign", "url: " + currentUrl);
        // TODO: 16/08/2017 两步验证
        if (currentUrl.startsWith("https://www.v2ex.com/mission/daily")) {
            mWebView.getSettings().setUserAgentString(APIService.USER_AGENT);
            doGetUserInfo();
            return true;
        }
        return super.checkIntercept(currentUrl);
    }

    private void doGetUserInfo() {
        APIService.get().dailyInfo()
                .compose(rx())
                .subscribe(new GeneralConsumer<DailyInfo>() {
                    @Override
                    public void onConsume(DailyInfo checkInInfo) {
                        toast("登录成功");
                        UserUtils.saveLogin(UserInfo.build(checkInInfo.getUserName(), checkInInfo.getAvatar()));
                        Navigator.from(SignInWithGoogleActivity.this)
                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .to(MainActivity.class).start();
                        CrashReport.setUserId(checkInInfo.getUserName());
                        finish();
                    }
                });
    }


}
