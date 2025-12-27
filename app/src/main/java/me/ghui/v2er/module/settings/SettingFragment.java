package me.ghui.v2er.module.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.StringRes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Adapter;
import android.widget.ListAdapter;

import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.imgur.MyUploadsActivity;
import me.ghui.v2er.network.imgur.ImgurConstants;
import me.ghui.v2er.util.Check;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.FrameLayout;
import me.ghui.v2er.bus.event.TextSizeChangeEvent;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Page;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.util.GlideCatchUtil;
import me.ghui.v2er.util.UserUtils;
import android.util.TypedValue;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ghui on 10/06/2017.
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private static final float SCALING_THRESHOLD = 0.01f;
    private ListView mListView;
    private android.widget.AbsListView.OnScrollListener mScrollListener;
    private Preference cachePref;
    private Preference loginPreference;
    private Preference imgurClientIdPref;

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
//        Preference updatePrefItem = findPreference(getString(R.string.pref_key_check_update));
//        updatePrefItem.setOnPreferenceClickListener(this);
//        updatePrefItem.setSummary("Version " + Utils.getVersionName());
        loginPreference = findPreference(getString(R.string.pref_key_value_toggle_log));
        loginPreference.setOnPreferenceClickListener(this);
        loginPreference.setTitle(UserUtils.isLogin() ? R.string.logout_str : R.string.login_str);
        findPreference(getString(R.string.pref_key_auto_checkin)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_highlight_topic_owner_reply_item)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_is_scan_in_reverse)).setOnPreferenceClickListener(this::onPreferenceClick);
        findPreference(getString(R.string.pref_key_v2er_app)).setOnPreferenceClickListener(this::onPreferenceClick);
        findPreference(getString(R.string.pref_key_auto_dark_mode_settings)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_contact_me_tg)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_contact_me_twitter)).setOnPreferenceClickListener(this);
//        findPreference(getString(R.string.pref_key_open_source)).setOnPreferenceClickListener(this);
        // Imgur settings
        imgurClientIdPref = findPreference(getString(R.string.pref_key_imgur_client_id));
        imgurClientIdPref.setOnPreferenceClickListener(this);
        updateImgurClientIdSummary();
        findPreference(getString(R.string.pref_key_my_uploads)).setOnPreferenceClickListener(this);
        ListPreference fontItem = (ListPreference) findPreference(getString(R.string.pref_key_fontsize));
        fontItem.setSummary(fontItem.getValue());
        fontItem.setOnPreferenceChangeListener((preference, newValue) -> {
            fontItem.setSummary(newValue + "");
            // Clear all scaling tags before applying new scaling
            clearFontScalingTags();
            // Post event will trigger onTextSizeChange to reapply scaling
            Bus.post(new TextSizeChangeEvent(FontSizeUtil.getContentSize()));
            return true;
        });

        // App version
        Preference versionPref = findPreference(getString(R.string.pref_key_app_version));
        versionPref.setSummary(Utils.getVersionName());
    }

    @Override
    public void onStart() {
        super.onStart();
        Bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Bus.unRegister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        rootView.setBackgroundColor(Theme.getColor(R.attr.page_bg_color, getActivity()));
        mListView = rootView.findViewById(android.R.id.list);
        if (mListView != null) {
            mListView.setDivider(null);
//            mListView.setDivider(getActivity().getDrawable(R.drawable.common_divider));
            Utils.setPaddingForNavbar(mListView);

            // Apply font scaling to preference items
            applyFontScalingToPreferences(mListView);
        }
    }

    private void applyFontScalingToPreferences(ListView listView) {
        // Apply font scaling after a short delay to ensure views are created
        listView.postDelayed(() -> {
            float scalingRatio = FontSizeUtil.getScalingRatio();
            if (Math.abs(scalingRatio - 1.0f) < SCALING_THRESHOLD) {
                // No scaling needed for default size
                return;
            }

            // Apply to all visible items
            for (int i = 0; i < listView.getChildCount(); i++) {
                View child = listView.getChildAt(i);
                applyFontScalingToView(child);
            }

            // Add scroll listener to apply scaling to new items (only if not already set)
            if (mScrollListener == null) {
                mScrollListener = new android.widget.AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
                    // Not needed
                }

                @Override
                public void onScroll(android.widget.AbsListView view, int firstVisibleItem,
                                       int visibleItemCount, int totalItemCount) {
                    // Apply scaling to newly visible items
                    for (int i = 0; i < visibleItemCount; i++) {
                        View child = view.getChildAt(i);
                        if (child != null && child.getTag(R.id.font_scaled_tag) == null) {
                            applyFontScalingToView(child);
                            child.setTag(R.id.font_scaled_tag, true);
                        }
                    }
                }
                };
                listView.setOnScrollListener(mScrollListener);
            }
        }, 100);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextSizeChange(TextSizeChangeEvent event) {
        // Clear all existing scaling and reapply with new size
        if (mListView != null) {
            // Clear tags and restore original sizes
            clearFontScalingTags();
            // Small delay to ensure views are restored before reapplying
            mListView.postDelayed(() -> {
                applyFontScalingToPreferences(mListView);
            }, 50);
        }
    }

    private void clearFontScalingTags() {
        if (mListView == null) return;

        // Clear tags from all visible items
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View child = mListView.getChildAt(i);
            clearTagsRecursively(child);
        }
    }

    private void clearTagsRecursively(View view) {
        if (view == null) return;

        // Reset text size to original if it's a TextView (BEFORE clearing tags)
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            Object originalSize = textView.getTag(R.id.original_text_size_tag);
            if (originalSize instanceof Float) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) originalSize);
            }
        }

        // Clear the scaling tags AFTER restoring original size
        view.setTag(R.id.font_scaled_tag, null);
        view.setTag(R.id.original_text_size_tag, null);

        // Recursively clear children
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                clearTagsRecursively(viewGroup.getChildAt(i));
            }
        }
    }

    private void applyFontScalingToView(View view) {
        if (view == null) return;

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Check if already scaled
            if (textView.getTag(R.id.original_text_size_tag) == null) {
                float originalSize = textView.getTextSize();
                textView.setTag(R.id.original_text_size_tag, originalSize);
                float scaledSize = FontSizeUtil.getScaledSize(originalSize);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledSize);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                applyFontScalingToView(child);
            }
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
            Utils.openWap("https://t.me/+Qb5d1yMWKdZ9P7Zj", getActivity());
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
            Utils.openInBrowser("https://v2er.app/help", getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_twitter))) {
            Utils.jumpToTwitterProfilePage(getActivity());
        } else if (key.equals(getString(R.string.pref_key_contact_me_tg))) {
            Utils.openWap("https://t.me/+Qb5d1yMWKdZ9P7Zj", getActivity());
        }  else if (key.equals(getString(R.string.pref_key_open_source))) {
            Utils.openWap("https://github.com/v2er-app", getActivity());
        } else if (key.equals(getString(R.string.pref_key_imgur_client_id))) {
            showImgurClientIdDialog();
            return true;
        } else if (key.equals(getString(R.string.pref_key_my_uploads))) {
            MyUploadsActivity.open(getActivity());
            return true;
        }
        return false;
    }

    private void updateImgurClientIdSummary() {
        String clientId = Pref.read(ImgurConstants.PREF_KEY_IMGUR_CLIENT_ID);
        if (Check.notEmpty(clientId)) {
            String maskedId = clientId.length() > 8
                    ? clientId.substring(0, 8) + "..."
                    : clientId;
            imgurClientIdPref.setSummary(getString(R.string.imgur_client_id_summary_custom, maskedId));
        } else {
            imgurClientIdPref.setSummary(R.string.imgur_client_id_summary_default);
        }
    }

    private void showImgurClientIdDialog() {
        String currentClientId = Pref.read(ImgurConstants.PREF_KEY_IMGUR_CLIENT_ID);

        EditText editText = new EditText(getActivity());
        editText.setHint(R.string.imgur_client_id_dialog_hint);
        if (Check.notEmpty(currentClientId)) {
            editText.setText(currentClientId);
            editText.setSelection(currentClientId.length());
        }

        FrameLayout container = new FrameLayout(getActivity());
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(editText);

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.imgur_client_id_dialog_title)
                .setView(container)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String newClientId = editText.getText().toString().trim();
                    Pref.save(ImgurConstants.PREF_KEY_IMGUR_CLIENT_ID, newClientId);
                    updateImgurClientIdSummary();
                    Voast.show(getString(R.string.imgur_client_id_saved));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
