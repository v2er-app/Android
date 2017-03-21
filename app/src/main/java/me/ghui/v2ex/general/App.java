package me.ghui.v2ex.general;

import android.app.Application;

/**
 * Created by ghui on 05/03/2017.
 */

public class App extends Application {
	private static App INSTANCE;

	public static App getInstance() {
		return INSTANCE;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (INSTANCE == null) {
			INSTANCE = this;
		}
	}
}
