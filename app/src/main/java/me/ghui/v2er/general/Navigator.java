package me.ghui.v2er.general;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.view.View;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by ghui on 29/03/2017.
 */

public class Navigator {

    private static Navigator navigator;
    private WeakReference<Context> mFrom;
    private Intent mIntent;
    private ActivityOptionsCompat mOptionsCompat;

    private Navigator(Context context) {
        mFrom = new WeakReference<>(context);
        mIntent = new Intent();
    }

    public static Navigator from(Context context) {
        return navigator = new Navigator(context);
    }

    public <T extends Activity> Navigator to(Class<T> page) {
        Context context = mFrom.get();
        if (context != null) {
            ComponentName component = new ComponentName(context, page);
            mIntent.setComponent(component);
        }
        return navigator;
    }

    public <T extends Activity> Navigator to(Page pageID) {
        to(PageHost.class);
        putExtra(PageHost.PAGE_ID, pageID);
        return navigator;
    }

    public void start() {
        Context context = mFrom.get();
        if (context != null) {
            if (mOptionsCompat != null) {
                context.startActivity(mIntent, mOptionsCompat.toBundle());
            } else {
                context.startActivity(mIntent);
            }
        }
        clear();
    }

    private void clear() {
        mFrom.clear();
        mFrom = null;
        navigator = null;
    }

    public Navigator setFlag(int flag) {
        mIntent.setFlags(flag);
        return this;
    }

    public Navigator addFlag(int flag) {
        mIntent.addFlags(flag);
        return this;
    }

    public Navigator shareElement(View... sourceViews) {
        Pair<View, String>[] sharedElements;
        List<View> sourceViewList = new ArrayList<>(sourceViews.length);
        Collections.addAll(sourceViewList, sourceViews);
        sourceViewList.removeIf(sourceView -> sourceView.getTransitionName() == null || sourceView.getTransitionName().isEmpty());
        sharedElements = new Pair[sourceViewList.size()];
        for (int i = 0; i < sourceViewList.size(); i++) {
            sharedElements[i] = new Pair<>(sourceViewList.get(i),
                    sourceViewList.get(i).getTransitionName());
        }
        if (sharedElements.length > 0) {
            mOptionsCompat = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mFrom.get(), sharedElements);
        }
        return this;
    }

    public Navigator shareElement(View sourceView) {
        if (sourceView != null && sourceView.getTransitionName() != null) {
            mOptionsCompat = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mFrom.get(), sourceView, sourceView.getTransitionName());
        }
        return this;
    }

    public Navigator putExtra(String key, Object value) {
        if (value == null) return this;
        Type type = value.getClass();
        if (type == int.class || type == Integer.class) {
            mIntent.putExtra(key, (int) value);
        } else if (type == boolean.class) {
            mIntent.putExtra(key, (boolean) value);
        } else if (type == String.class) {
            mIntent.putExtra(key, (String) value);
        } else if (value instanceof Serializable) {
            mIntent.putExtra(key, (Serializable) value);
        } else {
            new Exception("Navigator doesn't support " + type + " type").printStackTrace();
        }
        return this;
    }

}

