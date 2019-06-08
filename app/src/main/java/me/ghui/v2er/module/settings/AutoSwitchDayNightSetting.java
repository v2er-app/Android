package me.ghui.v2er.module.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import me.ghui.v2er.R;

public class AutoSwitchDayNightSetting extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private TimePreference dayStartTimePref;
    private TimePreference nightStartTimePref;

    public static AutoSwitchDayNightSetting newInstance() {
        Bundle args = new Bundle();
        AutoSwitchDayNightSetting fragment = new AutoSwitchDayNightSetting();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.auto_switch_daynight);
        SwitchPreference daynightSwitch = (SwitchPreference) findPreference(getString(R.string.pref_key_auto_daynight_switch));
        dayStartTimePref = (TimePreference) findPreference(getString(R.string.pref_key_day_mode_start_time));
        nightStartTimePref = (TimePreference) findPreference(getString(R.string.pref_key_night_mode_start_time));
        daynightSwitch.setOnPreferenceChangeListener(this);
        dayStartTimePref.setOnPreferenceChangeListener(this);
        nightStartTimePref.setOnPreferenceChangeListener(this);
        boolean enable = daynightSwitch.isChecked();
        dayStartTimePref.setEnabled(enable);
        nightStartTimePref.setEnabled(enable);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_auto_daynight_switch))) {
            // enable/disable
            boolean enable = (boolean) newValue;
            dayStartTimePref.setEnabled(enable);
            nightStartTimePref.setEnabled(enable);
        } else if (key.equals(getString(R.string.pref_key_day_mode_start_time))) {
            // day
        } else if (key.equals(getString(R.string.pref_key_night_mode_start_time))) {
           // night
        }
        return true;
    }
}
