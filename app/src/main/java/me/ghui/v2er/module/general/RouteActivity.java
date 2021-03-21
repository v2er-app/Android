package me.ghui.v2er.module.general;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.module.shortcuts.CreateTopicShortcut;
import me.ghui.v2er.module.shortcuts.DailyHotShortcut;
import me.ghui.v2er.module.shortcuts.StarTopicShortcut;
import me.ghui.v2er.network.UrlInterceptor;
import me.ghui.v2er.util.DayNightUtil;

/**
 * Created by ghui on 30/06/2017.
 */

public class RouteActivity extends Activity {


    protected void initTheme() {
        switch (DayNightUtil.getMode()) {
            case DayNightUtil.NIGHT_MODE:
                setTheme(R.style.NightTheme);
                break;
            case DayNightUtil.DAY_MODE:
            default:
                setTheme(R.style.DayTheme);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        if (DayNightUtil.isNightMode()) {
//            changeBrightness();
//        }
        initTheme();
        super.onCreate(savedInstanceState);
        route();
        finish();
    }

    private void changeBrightness() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = 0.3f;
        window.setAttributes(lp);
    }

    private void route() {
        Uri data = getIntent().getData();
        if (data == null) {
            Navigator.from(this).to(MainActivity.class).start();
            return;
        }

        switch (data.getScheme()) {
            case "https":
            case "http":
                // TODO: 15/10/2017 black window when entered
                UrlInterceptor.openWapPage(data.getPath(), this);
                return;
            case "v2er":
                v2erRoute(data);
                return;
        }
    }

    private void v2erRoute(Uri data) {
        switch (data.getPath()) {
            case "/daily_hot":
                Navigator.from(this).to(DailyHotShortcut.class).start();
                break;
            case "/create_topic":
                Navigator.from(this).to(CreateTopicShortcut.class).start();
                break;
            case "/my_star":
                Navigator.from(this).to(StarTopicShortcut.class).start();
                break;
        }
    }
}
