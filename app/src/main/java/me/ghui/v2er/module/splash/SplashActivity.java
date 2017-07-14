package me.ghui.v2er.module.splash;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.util.LightStatusBarUtils;

/**
 * Created by ghui on 26/06/2017.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LightStatusBarUtils.setLightStatusBar(getWindow(), true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Navigator.from(this).to(MainActivity.class).start();
        finish();
    }
}
