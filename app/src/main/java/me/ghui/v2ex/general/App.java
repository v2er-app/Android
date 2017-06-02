package me.ghui.v2ex.general;

import android.app.Application;

import com.orhanobut.logger.Logger;
import com.zzhoujay.richtext.RichText;

import me.ghui.v2ex.injector.component.AppComponent;
import me.ghui.v2ex.injector.component.DaggerAppComponent;
import me.ghui.v2ex.injector.module.AppModule;
import me.ghui.v2ex.network.APIService;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {

    private static App sInstance;
    private AppComponent mAppComponent;

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
        APIService.init();
        Logger.init().methodCount(1).hideThreadInfo();
        RichText.initCacheDir(getCacheDir());
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
