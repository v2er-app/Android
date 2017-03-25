package me.ghui.v2ex.general;

import android.app.Application;

import me.ghui.v2ex.injector.component.AppComponent;
import me.ghui.v2ex.injector.component.DaggerAppComponent;
import me.ghui.v2ex.injector.module.AppModule;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {

	private static App sInstance;
	private AppComponent mAppComponent;

	public static App getInstance() {
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
	}

	public AppComponent getAppComponent() {
		return mAppComponent;
	}
}
