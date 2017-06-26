package me.ghui.v2er.module.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import me.ghui.v2er.widget.dialog.BaseDialog;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 10/06/2017.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private Preference cachePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        cachePref = findPreference(getString(R.string.pref_key_clear_cache));
        cachePref.setOnPreferenceClickListener(this);
        cachePref.setSummary(String.format(getString(R.string.cache_summary) + "（共%s）", GlideCatchUtil.getCacheSize()));
        findPreference(getString(R.string.pref_key_check_update)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_rate)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_value_toggle_log)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_send_email)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_weibo_personal_page)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_twitter_personal_page)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        if (list != null) {
            list.setDivider(null);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_clear_cache))) {
            String size = GlideCatchUtil.getCacheSize();
            boolean ok = GlideCatchUtil.clearDiskCache();
            if (ok) {
                cachePref.setSummary(getString(R.string.cache_summary));
                Utils.toast("成功清理" + size + "缓存");
            }
            return true;
        } else if (key.equals(getString(R.string.pref_key_check_update))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_rate))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_value_toggle_log))) {
            new ConfirmDialog.Builder(getActivity())
                    .title("退出登录")
                    .msg("确定退出吗？")
                    .positiveText(R.string.ok, dialog -> {
                        UserUtils.clearLogin();
                        Navigator.from(getActivity())
                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .to(MainActivity.class).start();
                    })
                    .negativeText(R.string.cancel)
                    .build().show();
            return true;
        } else if (key.equals(getString(R.string.pref_send_email))) {
            Utils.sendEmail(getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_weibo_personal_page))) {
            Utils.jumpToWeiboProfileInfo(getContext());
            return true;
        } else if (key.equals(getString(R.string.pref_twitter_personal_page))) {
            Utils.jumpToTwitterProfilePage();
            return true;
        }
        return false;
    }
}
