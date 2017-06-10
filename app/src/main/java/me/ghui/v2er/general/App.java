package me.ghui.v2er.general;

import android.app.Application;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.orhanobut.logger.Logger;
import com.zzhoujay.richtext.RichText;

import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.injector.component.DaggerAppComponent;
import me.ghui.v2er.injector.module.AppModule;
import me.ghui.v2er.network.APIService;

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
        initInstabug();
    }

    private void initInstabug() {
        new Instabug.Builder(this, "5d8a77e264fabc79ca83c82b95c6e1a0")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
