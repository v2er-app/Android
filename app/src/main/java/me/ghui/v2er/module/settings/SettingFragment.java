package me.ghui.v2er.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.StringRes;

import android.view.View;
import android.widget.ListView;

import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Page;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.util.GlideCatchUtil;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 10/06/2017.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference cachePref;
    private Preference loginPreference;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
//        Bus.register(this);
        cachePref = findPreference(getString(R.string.pref_key_clear_cache));
        cachePref.setOnPreferenceClickListener(this);
        cachePref.setSummary(String.format(getString(R.string.cache_summary) + "（共%s）", GlideCatchUtil.getCacheSize()));
        Preference updatePrefItem = findPreference(getString(R.string.pref_key_check_update));
        updatePrefItem.setOnPreferenceClickListener(this);
        updatePrefItem.setSummary("Version " + Utils.getVersionName());
        loginPreference = findPreference(getString(R.string.pref_key_value_toggle_log));
        loginPreference.setOnPreferenceClickListener(this);
        loginPreference.setTitle(UserUtils.isLogin() ? R.string.logout_str : R.string.login_str);
        findPreference(getString(R.string.pref_key_auto_checkin)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_highlight_topic_owner_reply_item)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_is_scan_in_reverse)).setOnPreferenceClickListener(this::onPreferenceClick);
        findPreference(getString(R.string.pref_key_v2er_app)).setOnPreferenceClickListener(this::onPreferenceClick);
        findPreference(getString(R.string.pref_key_auto_dark_mode_settings)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_contact_me_tg)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_open_source)).setOnPreferenceClickListener(this);
        ListPreference fontItem = (ListPreference) findPreference(getString(R.string.pref_key_fontsize));
        fontItem.setSummary(fontItem.getValue());
        fontItem.setOnPreferenceChangeListener((preference, newValue) -> {
            fontItem.setSummary(newValue + "");
            Bus.post(new TextSizeChangeEvent(FontSizeUtil.getContentSize()));
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        rootView.setBackgroundColor(Theme.getColor(R.attr.page_bg_color, getActivity()));
        ListView list = rootView.findViewById(android.R.id.list);
        if (list != null) {
            list.setDivider(null);
//            list.setDivider(getActivity().getDrawable(R.drawable.common_divider));
            Utils.setPaddingForNavbar(list);
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
            Utils.sendOfficalV2erEmail(getActivity());
            return true;
        }  else if (key.equals(getString(R.string.pref_key_v2ex))) {
            Utils.openWap(getString(R.string.official_v2ex_about_website), getActivity());
        } else if (key.equals(getString(R.string.pref_key_user_group))) {
            Utils.openWap("https://t.me/v2er_app", getActivity());
        } else if (key.equals(getString(R.string.pref_key_auto_dark_mode_settings))) {
//            Bus.post(new DayNightModeEvent(isItemChecked(preference)));
            Navigator.from(getContext()).to(Page.AUTO_SWITCH_DARK_MODE).start();
        } else if (key.equals(getString(R.string.pref_key_email))) {
            Utils.sendOfficalV2erEmail(getActivity());
        } else if (key.equals(getString(R.string.pref_key_rate))) {
            new ConfirmDialog.Builder(getActivity())
                    .title("V2er好用吗？")
                    .msg("V2er需要你的支持，你可以选择去商店给V2er一个5星好评。")
                    .positiveText("去支持！", dialog -> Utils.openStorePage())
                    .negativeText("暂不")
                    .build().show();
        } else if (key.equals(getString(R.string.pref_key_contact))) {
            Navigator.from(getContext()).to(Page.CONTACT).start();
        } else if (key.equals(getString(R.string.pref_key_v2er_app))) {
            Utils.openInBrowser("https://v2er.app", getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_twitter))) {
            Utils.jumpToTwitterProfilePage(getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_tg))) {
            Utils.openWap("https://t.me/v2er_app", getActivity());
        }  else if (key.equals(getString(R.string.pref_key_open_source))) {
            Utils.openWap("https://github.com/v2er-app", getActivity());
        }
        return false;
    }

    private boolean isItemChecked(Preference preference) {
        CheckBoxPreference switchPreference = (CheckBoxPreference) preference;
        return switchPreference.isChecked();
    }

    private boolean strEquals(String str, @StringRes int... strId) {
        for (int id : strId) {
            if (str.equals(getString(id))) return true;
        }
        return false;
    }

}
