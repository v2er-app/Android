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
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.util.GlideCatchUtil;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 10/06/2017.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Preference cachePref;
    private Preference loginPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        cachePref = findPreference(getString(R.string.pref_key_clear_cache));
        cachePref.setOnPreferenceClickListener(this);
        cachePref.setSummary(String.format(getString(R.string.cache_summary) + "（共%s）", GlideCatchUtil.getCacheSize()));
        Preference updatePrefItem = findPreference(getString(R.string.pref_key_check_update));
        updatePrefItem.setOnPreferenceClickListener(this);
        updatePrefItem.setSummary(String.format("当前版本 " + Utils.getVersionName() + " (" + Utils.getVersionCode() + ")"));
        findPreference(getString(R.string.pref_key_rate)).setOnPreferenceClickListener(this);
        loginPreference = findPreference(getString(R.string.pref_key_value_toggle_log));
        loginPreference.setOnPreferenceClickListener(this);
        loginPreference.setTitle(UserUtils.isLogin() ? R.string.logout_str : R.string.login_str);
        findPreference(getString(R.string.pref_send_email)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_weibo_personal_page)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_twitter_personal_page)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_value_copyright)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_v2ex)).setOnPreferenceClickListener(this);
//        findPreference(getString(R.string.pref_key_os)).setOnPreferenceClickListener(this);
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
                Voast.show("成功清理" + size + "缓存");
            }
            return true;
        } else if (key.equals(getString(R.string.pref_key_check_update))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_rate))) {
            Utils.openStorePage();
            return true;
        } else if (key.equals(getString(R.string.pref_key_value_toggle_log))) {
            if (!UserUtils.isLogin()) {
                Navigator.from(getActivity()).to(LoginActivity.class).start();
                return true;
            }
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
            Utils.sendEmail(getActivity());
            return true;
        } else if (key.equals(getString(R.string.pref_weibo_personal_page))) {
            Utils.jumpToWeiboProfileInfo(getActivity());
            return true;
        } else if (key.equals(getString(R.string.pref_twitter_personal_page))) {
            Utils.jumpToTwitterProfilePage(getActivity());
            return true;
        } else if (key.equals(getString(R.string.pref_key_value_copyright))) {
            Utils.openWap(getString(R.string.official_website), getActivity());
        } else if (key.equals(getString(R.string.pref_key_v2ex))) {
            Utils.openWap(getString(R.string.official_v2ex_about_website), getActivity());
        }
        return false;
    }
}
