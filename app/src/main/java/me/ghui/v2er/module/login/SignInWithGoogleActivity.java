package me.ghui.v2er.module.login;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebSettings;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.general.WapActivity;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.DailyInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 13/08/2017.
 */

public class SignInWithGoogleActivity extends WapActivity {
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
    protected void configWebView(WebSettings settings) {
        super.configWebView(settings);
        settings.setUserAgentString(APIService.WAP_Android_USER_AGENT);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
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
        L.d("url: " + currentUrl);
        if (currentUrl.startsWith(Constants.BASE_URL + "/mission/daily")) {
            mWebView.getSettings().setUserAgentString(APIService.WAP_Android_USER_AGENT);
            doGetUserInfo();
            return true;
        }
        return super.checkIntercept(currentUrl);
    }

    private void doGetUserInfo() {
        APIService.get().dailyInfo()
                .compose(rx())
                .subscribe(new GeneralConsumer<DailyInfo>(this) {
                    @Override
                    public void onConsume(DailyInfo checkInInfo) {
                        toast("登录成功");
                        UserUtils.saveLogin(UserInfo.build(checkInInfo.getUserName(), checkInInfo.getAvatar()));
                        Navigator.from(SignInWithGoogleActivity.this)
                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .to(MainActivity.class).start();
                        FlurryAgent.setUserId(checkInInfo.getUserName());
                        finish();
                    }
                });
    }
}
