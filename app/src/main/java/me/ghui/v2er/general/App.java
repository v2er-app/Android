package me.ghui.v2er.general;

import android.app.Application;
import android.preference.PreferenceManager;

import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.R;
import me.ghui.v2er.VariantConstants;
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
        Logger.init().methodCount(1).hideThreadInfo();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        APIService.init();
        initBugly();
        initWechat();
    }

    private void initBugly() {
        if (BuildConfig.DEBUG) return;
        CrashReport.initCrashReport(getApplicationContext(), VariantConstants.BUGLY_ID, BuildConfig.DEBUG);
        if (UserUtils.isLogin()) {
            CrashReport.setUserId(UserUtils.getUserInfo().getUserName());
        } else {
            CrashReport.setUserId("UnLogin");
        }
    }

    private void initWechat() {
        mWechat = WXAPIFactory.createWXAPI(this, null);
        mWechat.registerApp(VariantConstants.WECHAT_ID);
    }

    public IWXAPI wechat() {
        return mWechat;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
