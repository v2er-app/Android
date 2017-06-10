package me.ghui.v2er.general;

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
        return navigator = new Navigator(context);
    }

    public <T extends Activity> Navigator to(Class<T> page) {
        mTo = page;
        return navigator;
    }

    public void start() {
        Context context = mFrom.get();
        if (context != null) {
            Intent intent = new Intent(context, mTo);
            if (mExtras != null) {
                intent.putExtras(mExtras);
            }
            context.startActivity(intent);
        }
        clear();
    }

    private void clear() {
        mFrom.clear();
        mFrom = null;
        mTo = null;
        mExtras = null;
        navigator = null;
    }

    private Bundle getExtras() {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        return mExtras;
    }

    public Navigator putExtra(String name, boolean value) {
        getExtras().putBoolean(name, value);
        return this;
    }

    public Navigator putExtra(String name, String value) {
        getExtras().putString(name, value);
        return this;
    }
    public Navigator putExtra(String name, int value) {
        getExtras().putInt(name, value);
        return this;
    }

}

