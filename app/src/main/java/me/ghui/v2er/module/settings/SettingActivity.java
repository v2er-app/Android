package me.ghui.v2er.module.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.general.SlideBackManager;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
//        Utils.transparentBars(getWindow());
        setContentView(R.layout.act_setting);
        SlideBackManager.attach(this);
        getFragmentManager().beginTransaction()
                .add(R.id.pref_rootview, SettingFragment.newInstance())
                .commit();
        BaseToolBar toolBar = findViewById(R.id.inner_toolbar);
        Utils.setPaddingForStatusBar(toolBar);
        toolBar.setTitle(getTitle());
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected void initTheme() {
        switch (DayNightUtil.getMode()) {
            case DayNightUtil.NIGHT_MODE:
                setTheme(R.style.NightTheme);
                break;
            case DayNightUtil.AUTO_MODE:
                break;
            case DayNightUtil.DAY_MODE:
            default:
                setTheme(R.style.DayTheme);
                break;
        }
    }
}
