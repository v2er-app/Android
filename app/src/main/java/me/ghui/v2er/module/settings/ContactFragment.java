package me.ghui.v2er.module.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.annotation.StringRes;
import android.view.View;
import android.widget.ListView;

import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

public class ContactFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static ContactFragment newInstance() {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.contact_preferences);
        findPreference(R.string.pref_key_contact_me_twitter).setOnPreferenceClickListener(this);
        findPreference(R.string.pref_key_contact_me_tg).setOnPreferenceClickListener(this);
        Preference wechatgroupItem = findPreference(R.string.pref_key_contact_me_wechat);
        if (UserUtils.isPro()) {
            wechatgroupItem.setOnPreferenceClickListener(this);
        } else {
            getPreferenceScreen().removePreference(wechatgroupItem);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        rootView.setBackgroundColor(Theme.getColor(R.attr.page_bg_color, getActivity()));
        ListView list = rootView.findViewById(android.R.id.list);
        if (list != null) {
//            list.setDivider(getActivity().getDrawable(R.drawable.common_divider));
            Utils.setPaddingForNavbar(list);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_contact_me_jike))) {
            Utils.jumpToJikeProfileInfo(getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_tg))) {
            Utils.openWap("https://t.me/v2er_app", getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_weibo))) {
            Utils.jumpToWeiboProfileInfo(getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_mail))) {
            Utils.sendOfficalV2erEmail(getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_wechat))) {
            // copy wechat to clipboard
            Utils.copy2Clipboard(getString(R.string.wechat_username));
            Voast.show("已复制微信号(lessmoreio)，请加我为好友后我拉你入群", true);
        } else if (key.equals(getString(R.string.pref_key_contact_me_twitter))) {
            Utils.jumpToTwitterProfilePage(getActivity());
        }
        return true;
    }

    private Preference findPreference(@StringRes int prefKey) {
        return findPreference(getString(prefKey));
    }

}
