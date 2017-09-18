package me.ghui.v2er.module.settings;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.general.SlideBackManager;
import me.ghui.v2er.util.LightStatusBarUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LightStatusBarUtils.setLightStatusBar(getWindow(), true);
        Utils.transparentBars(getWindow(), Color.TRANSPARENT, getResources().getColor(R.color.transparent_navbar_color));
        setContentView(R.layout.act_setting);
        SlideBackManager.attach(this);
        getFragmentManager().beginTransaction()
                .add(R.id.pref_rootview, SettingFragment.newInstance())
                .commit();
        BaseToolBar toolBar = (BaseToolBar) findViewById(R.id.inner_toolbar);
        Utils.setPaddingForStatusBar(toolBar);
        toolBar.setTitle(getTitle());
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
