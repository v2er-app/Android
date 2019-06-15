package me.ghui.v2er.general;

import android.app.Activity;
import android.content.Intent;

import java.io.Serializable;
import java.lang.reflect.Type;

import me.ghui.v2er.R;

public class ColorModeReloader {
    private Intent mIntent;
    private Activity mActivity;


    private ColorModeReloader(Activity mTarget) {
        mActivity = mTarget;
        this.mIntent = new Intent(mTarget, mTarget.getClass());
    }

    public static ColorModeReloader target(Activity target) {
        return new ColorModeReloader(target);
    }

    public ColorModeReloader putExtra(String key, Object value) {
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

    public void reload() {
        mActivity.startActivity(mIntent);
        mActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        mActivity.finish();
    }


}
