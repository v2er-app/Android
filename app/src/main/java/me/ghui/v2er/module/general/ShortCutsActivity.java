package me.ghui.v2er.module.general;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;

/**
 * Created by ghui on 21/09/2017.
 */

public class ShortCutsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        route();
        finish();
    }

    private void route() {
        String action = getIntent().getAction();
        switch (action) {
            case "shortcuts_daily_hot":
                Navigator.from(this).to(DailyHotActivity.class).start();
                break;
        }
    }
}
