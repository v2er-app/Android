package me.ghui.v2er.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ListView;

import me.ghui.toolbox.android.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.AutoDayNightModeEvent;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.BillingManager;
import me.ghui.v2er.general.Navigator;
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
//        Bus.register(this);
        cachePref = findPreference(getString(R.string.pref_key_clear_cache));
        cachePref.setOnPreferenceClickListener(this);
        cachePref.setSummary(String.format(getString(R.string.cache_summary) + "（共%s）", GlideCatchUtil.getCacheSize()));
        Preference updatePrefItem = findPreference(getString(R.string.pref_key_check_update));
        updatePrefItem.setOnPreferenceClickListener(this);
        updatePrefItem.setSummary("当前版本 " + Utils.getVersionName());
        loginPreference = findPreference(getString(R.string.pref_key_value_toggle_log));
        loginPreference.setOnPreferenceClickListener(this);
        loginPreference.setTitle(UserUtils.isLogin() ? R.string.logout_str : R.string.login_str);
        findPreference(getString(R.string.pref_key_auto_checkin)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_highlight_topic_owner_reply_item)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_user_group)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_rate)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_email)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_weibo_personal_page)).setOnPreferenceClickListener(this);
//        findPreference(getString(R.string.pref_key_auto_daynight)).setOnPreferenceClickListener(this);
        Preference proItem = findPreference(getString(R.string.pref_key_v2er_pro));
        proItem.setTitle(UserUtils.isPro() ? "Pro特性已开启" : "激活Pro特性");
        proItem.setSummary(UserUtils.isPro() ? "感谢支持" : "更多实用功能并能支持V2er的长期开发");
        proItem.setOnPreferenceClickListener(this);

        ListPreference fontItem = (ListPreference) findPreference(getString(R.string.pref_key_fontsize));
        fontItem.setSummary(fontItem.getValue());
        fontItem.setOnPreferenceChangeListener((preference, newValue) -> {
            fontItem.setSummary(newValue + "");
            Bus.post(new TextSizeChangeEvent(FontSizeUtil.getContentSize()));
            return true;
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        rootView.setBackgroundColor(Theme.getColor(R.attr.page_bg_color, getActivity()));
        ListView list = rootView.findViewById(android.R.id.list);
        if (list != null) {
            list.setDivider(null);
            Utils.setPaddingForNavbar(list);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (isFeatureUnavaliable(key)) {
            CheckBoxPreference switchPreference = (CheckBoxPreference) preference;
            switchPreference.setChecked(false);
            new ConfirmDialog.Builder(getActivity())
                    .title("功能不可用")
                    .msg("此功能是Pro版特性，激活Pro版以开启")
                    .positiveText("暂不")
                    .negativeText("去激活", dialog -> {
                        BillingManager.get().startPurchaseFlow(getActivity(), isSuccess -> {
                            String msg = isSuccess ? "激活成功!" : "激活失败";
                            Voast.show(msg);
                            Preference item = findPreference(key);
                            if (item instanceof CheckBoxPreference) {
                                ((CheckBoxPreference) item).setChecked(isSuccess);
                            }
                        });
                    })
                    .build().show();
            return true;
        }

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
        } else if (key.equals(getString(R.string.pref_weibo_personal_page))) {
            Utils.jumpToWeiboProfileInfo(getActivity());
            return true;
        } else if (key.equals(getString(R.string.pref_twitter_personal_page))) {
            Utils.jumpToTwitterProfilePage(getActivity());
            return true;
        } else if (key.equals(getString(R.string.pref_key_v2ex))) {
            Utils.openWap(getString(R.string.official_v2ex_about_website), getActivity());
        } else if (key.equals(getString(R.string.pref_key_v2er_pro))) {
            Navigator.from(getActivity()).to(ProInfoActivity.class).start();
        }  else if (key.equals(getString(R.string.pref_key_user_group))) {
            Utils.openWap("https://t.me/v2er_app", getActivity());
        } else if (key.equals(getString(R.string.pref_key_auto_daynight))) {
            Bus.post(new AutoDayNightModeEvent(isItemChecked(preference)));
        } else if (key.equals(getString(R.string.pref_key_email))) {
            Utils.sendOfficalV2erEmail(getActivity());
        } else if (key.equals(getString(R.string.pref_key_rate))) {
            new ConfirmDialog.Builder(getActivity())
                    .title("V2er好用吗？")
                    .msg("V2er需要你的支持，你可以选择去商店给V2er一个5星好评。")
                    .positiveText("去支持！", dialog -> Utils.openStorePage())
                    .negativeText("暂不")
                    .build().show();
        }
        return false;
    }

    private boolean isItemChecked(Preference preference) {
        CheckBoxPreference switchPreference = (CheckBoxPreference) preference;
        return switchPreference.isChecked();
    }

    private boolean isFeatureUnavaliable(String key) {
        return !UserUtils.isPro() && strEquals(key, R.string.pref_key_auto_checkin,
                R.string.pref_key_highlight_topic_owner_reply_item, R.string.pref_key_auto_daynight);
    }

    private boolean strEquals(String str, @StringRes int... strId) {
        for (int id : strId) {
            if (str.equals(getString(id))) return true;
        }
        return false;
    }


}
