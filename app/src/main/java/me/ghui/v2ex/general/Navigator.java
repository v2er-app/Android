package me.ghui.v2ex.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.lang.ref.WeakReference;


/**
 * Created by ghui on 29/03/2017.
 */

public class Navigator {

	private static Navigator navigator;
	private WeakReference<Context> mFrom;
	private Class mTo;
	private Bundle mExtras;

	private Navigator(Context context) {
		mFrom = new WeakReference<>(context);
	}

	public static Navigator from(Context context) {
		if (navigator == null) {
			navigator = new Navigator(context);
		}
		return navigator;
	}

	public <T extends Activity> Navigator to(Class<T> page) {
		mTo = page;
		return navigator;
	}

	public void start() {
		Intent intent = new Intent(mFrom.get(), mTo);
		mFrom.get().startActivity(intent);
		mFrom.clear();
		mTo = null;
	}

	public Navigator putExtra(String name, boolean value) {
		if (mExtras == null) {
			mExtras = new Bundle();
		}
		mExtras.putBoolean(name, value);
		return navigator;
	}


}
