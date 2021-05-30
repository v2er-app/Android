package me.ghui.v2er.module.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import androidx.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.general.PageHost;
import me.ghui.v2er.util.DarkModelUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Voast;

public class AutoSwitchDayNightSetting extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference mAutoDarkModeSwitcher;
    private ListPreference mAutoSwitchModePref;
    private TimePreference mDayStartTimePref;
    private TimePreference mNightStartTimePref;

    private int modeWhenEnter;

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
        modeWhenEnter = DarkModelUtils.getMode();


        mAutoDarkModeSwitcher = (SwitchPreference) findPreference(getString(R.string.pref_key_auto_dark_mode_switch));
        mAutoSwitchModePref = (ListPreference) findPreference(getString(R.string.pref_key_auto_dark_mode_way));
        mDayStartTimePref = (TimePreference) findPreference(getString(R.string.pref_key_day_mode_start_time));
        mNightStartTimePref = (TimePreference) findPreference(getString(R.string.pref_key_night_mode_start_time));

        // DCHECk
        if (!UserUtils.isPro()) {
            DarkModelUtils.saveEnableAutoSwitch(false);
            Voast.show("您不是高级用户，无法使用此功能");
            getActivity().finish();
            return;
        }

        mAutoDarkModeSwitcher.setOnPreferenceChangeListener(this);
        mAutoSwitchModePref.setOnPreferenceChangeListener(this);
        mDayStartTimePref.setOnPreferenceChangeListener(this);
        mNightStartTimePref.setOnPreferenceChangeListener(this);

        updateUI(false);
    }

    private void updateUI(boolean refreshMode) {
        boolean isPro = UserUtils.isPro();
        mAutoSwitchModePref.setSummary(mAutoSwitchModePref.getEntry());
        mAutoSwitchModePref.setEnabled(DarkModelUtils.isAutoModeEnabled());
        mDayStartTimePref.setEnabled(DarkModelUtils.isAutoChangeByTimeEnabled());
        mNightStartTimePref.setEnabled(DarkModelUtils.isAutoChangeByTimeEnabled());

        if (refreshMode) {
            int mode = DarkModelUtils.getMode();
            if (modeWhenEnter != mode && getActivity() instanceof PageHost) {
                PageHost pageHost = (PageHost) getActivity();
                pageHost.reloadMode(mode);
            }
        }
    }

    // newValue : 0 - 跟随系统, 1 - 设置时间
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_auto_dark_mode_switch))) {
            // AutoMode Enable/Disable
            boolean enable = (boolean) newValue;
            DarkModelUtils.saveEnableAutoSwitch(enable);
        } else if (key.equals(getString(R.string.pref_key_auto_dark_mode_way))) {
            // AutoSwitch Mode // follow system or follow time
            DarkModelUtils.saveAutoSwitchMode((String) newValue);
        }
        updateUI(true);
        return true;
    }

}
