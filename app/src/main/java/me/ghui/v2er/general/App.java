package me.ghui.v2er.general;

import android.app.Application;
import android.preference.PreferenceManager;

import com.flurry.android.FlurryAgent;
import com.oasisfeng.condom.CondomContext;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.User;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.R;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.injector.component.DaggerAppComponent;
import me.ghui.v2er.injector.module.AppModule;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {
    private static App sInstance;
    private AppComponent mAppComponent;
    private IWXAPI mWechat;

    public static App get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sInstance = this;
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(sInstance))
                .build();
        BillingManager.get().checkIsProAsyc();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.auto_switch_daynight, false);
        APIService.init();
        initThirdPartySDK();
    }

    private void initThirdPartySDK() {
        if (BuildConfig.DEBUG) return;
        initSentry();
        initFlurry();
        initWechat();
    }

    private void initFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .withCaptureUncaughtExceptions(false)
                .build(this, "4PZMS4HSZP3YNMBP8W4R");
        FlurryAgent.setUserId(UserUtils.getUserName());
    }

    private void initSentry() {
        String sentryDsn = "https://09079965b3d74fdba7d4e5b867df5df3@sentry.io/1476360";
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(this));
        User user = new User(null, UserUtils.getUserName(), null, null, null);
        Sentry.getContext().setUser(user);
    }

    private void initWechat() {
        mWechat = WXAPIFactory.createWXAPI(CondomContext.wrap(this, "wechat"), null);
        mWechat.registerApp("wxdb7f82c706f4516c");
    }

    public IWXAPI wechat() {
        return mWechat;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
