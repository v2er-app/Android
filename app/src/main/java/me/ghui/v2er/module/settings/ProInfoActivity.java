package me.ghui.v2er.module.settings;

import android.graphics.Color;
import android.view.Window;

import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Constants;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 11/09/2017.
 */

public class ProInfoActivity extends BaseActivity {

    @Override
    protected int attachLayoutRes() {
        return R.layout.pro_page;
    }

    @Override
    protected void configSystemBars(Window window) {
        Utils.transparentBars(window, false);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(mRootView);
        Utils.setPaddingForNavbar(mRootView);
        toolBar.setElevation(0);
        toolBar.setBackgroundColor(Color.TRANSPARENT);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.getNavigationIcon().setTint(Color.WHITE);
    }

    @Override
    protected int pageColor() {
        return 0xFF000000;
    }

    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        Utils.openStorePage(Constants.PKG_PRO);
    }

}
