package me.ghui.v2er.general;

import android.app.Fragment;

import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.settings.AutoSwitchDayNightSetting;
import me.ghui.v2er.module.settings.ContactFragment;
import me.ghui.v2er.module.settings.SettingFragment;
import me.ghui.v2er.util.Utils;

public class PageHost extends BaseActivity {
    public static final String PAGE_ID = "PageHost.pageId";

    @Override
    protected int attachLayoutRes() {
        return R.layout.host_page;
    }

    @Override
    protected void init() {
        super.init();
        Utils.setPaddingForStatusBar(mRootView);
        Page page = (Page) getIntent().getSerializableExtra(PAGE_ID);
        if (page == null) {
            throw new RuntimeException("wrong page id");
        }
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, getFragment(page))
                .commit();
    }

    private Fragment getFragment(Page pageID) {
        Fragment fragment;
        String title;
        switch (pageID) {
            case SETTING:
                fragment = SettingFragment.newInstance();
                title = "设置";
                break;
            case CONTACT:
                fragment = ContactFragment.newInstance();
                title = "联系";
                break;
            case AUTO_SWITCH_DAYNIGHT:
                fragment = AutoSwitchDayNightSetting.newInstance();
                title = "自动日夜";
                break;
            default:
                throw new RuntimeException("wrong page id: " + pageID);
        }
        setTitle(title);
        return fragment;
    }

}
