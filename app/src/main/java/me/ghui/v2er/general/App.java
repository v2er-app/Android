package me.ghui.v2er.general;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.flurry.android.FlurryAgent;
import com.oasisfeng.condom.CondomContext;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Calendar;

import io.reactivex.plugins.RxJavaPlugins;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.R;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.injector.component.DaggerAppComponent;
import me.ghui.v2er.injector.module.AppModule;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.util.Flurry;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {
    private static App sInstance;
    private AppComponent mAppComponent;
    private IWXAPI mWechat;
    public int unReadMsgCount = 0;

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
        rxjava();
        initLogger();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.auto_switch_daynight, false);
        APIService.init();
        initThirdPartySDK();
    }

    private void rxjava() {
        RxJavaPlugins.setErrorHandler(e -> {
            L.e("globalHandler: " + e.getMessage());
//            V2er.capture("globalHandler: " + e.getMessage());
            Flurry.capture("globalHandler: " + e.getMessage());
        });
    }

    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("V2er.Log")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    private void initThirdPartySDK() {
        initFlurry();
        initWechat();
    }

    private void initFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .withCaptureUncaughtExceptions(true)
                .build(this, "4PZMS4HSZP3YNMBP8W4R");
        FlurryAgent.setUserId(UserUtils.getUserName());
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
