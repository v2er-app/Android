package me.ghui.v2er.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.util.GlideCatchUtil;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 10/06/2017.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_key_clear_cache)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_check_update)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_rate)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_value_toggle_log)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        if (list != null) {
            list.setDivider(null);
            list.setFitsSystemWindows(true);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_clear_cache))) {
            String size = GlideCatchUtil.getCacheSize();
            boolean ok = GlideCatchUtil.clearDiskCache();
            if (ok) Utils.toast("成功清理" + size + "缓存");
            return true;
        } else if (key.equals(getString(R.string.pref_key_check_update))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_rate))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_value_toggle_log))) {
            // TODO: 11/06/2017 confirm dialog 
            UserUtils.clearLogin();
            Navigator.from(getActivity())
                    .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .to(MainActivity.class).start();
            return true;
        }
        return false;
    }
}
