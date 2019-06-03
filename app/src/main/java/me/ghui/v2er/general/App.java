package me.ghui.v2er.general;

import android.app.Application;
import android.preference.PreferenceManager;

import com.oasisfeng.condom.CondomContext;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
        Logger.init().methodCount(1).hideThreadInfo();
        BillingManager.get().checkIsProAsyc();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        APIService.init();
        initBugly();
        initWechat();
    }

    private void initWechat() {
        mWechat = WXAPIFactory.createWXAPI(CondomContext.wrap(this, "wechat"), null);
        mWechat.registerApp("wxdb7f82c706f4516c");
    }

    private void initBugly() {
        if (BuildConfig.DEBUG) return;
        CrashReport.initCrashReport(CondomContext.wrap(this, "bugly"), "b0ba618423", BuildConfig.DEBUG);
        if (UserUtils.isLogin()) {
            CrashReport.setUserId(UserUtils.getUserInfo().getUserName());
        } else {
            CrashReport.setUserId("UnLogin");
        }
    }

    public IWXAPI wechat() {
        return mWechat;
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
