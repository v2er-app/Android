package me.ghui.v2er.module.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.widget.BaseToolBar;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_setting);
        getFragmentManager().beginTransaction()
                .add(R.id.pref_rootview, SettingFragment.newInstance())
                .commit();
        BaseToolBar toolBar = (BaseToolBar) findViewById(R.id.pref_toolbar);
        toolBar.setTitle(getTitle());
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
