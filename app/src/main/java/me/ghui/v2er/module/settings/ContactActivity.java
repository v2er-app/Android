package me.ghui.v2er.module.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;


public class ContactActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        Utils.transparentBars(getWindow());
        setContentView(R.layout.contact_page);
        getFragmentManager().beginTransaction()
                .add(R.id.pref_rootview, ContactFragment.newInstance())
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