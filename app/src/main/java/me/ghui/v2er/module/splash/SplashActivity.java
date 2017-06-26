package me.ghui.v2er.module.splash;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.home.MainActivity;

/**
 * Created by ghui on 26/06/2017.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        Navigator.from(this).to(MainActivity.class).start();
        finish();
    }
}
