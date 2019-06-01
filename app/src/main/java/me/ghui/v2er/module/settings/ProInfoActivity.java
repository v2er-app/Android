package me.ghui.v2er.module.settings;

import android.graphics.Color;
import android.view.View;
import android.view.Window;

import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.BillingManager;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 11/09/2017.
 */

public class ProInfoActivity extends BaseActivity {

    private boolean isPro;

    @Override
    protected int attachLayoutRes() {
        return R.layout.pro_page;
    }

    @Override
    protected void configSystemBars(Window window) {
        Utils.transparentBars(window, false);
    }

    @Override
    protected void initTheme() {
        setTheme(R.style.NightTheme);
    }

    @Override
    protected void init() {
        super.init();
        isPro = UserUtils.isPro();
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
        if (isPro) {
            Voast.show("Pro已激活，感谢支持");
            return;
        }
        BillingManager.get().startPurchaseFlow(getActivity(), isSuccess -> {
            isPro = isSuccess;
            String msg = isSuccess ? "激活成功!" : "激活失败";
            Voast.show(msg);
        });
    }

    public void onNoResponseClicked(View view) {
        new ConfirmDialog.Builder(this)
                .title("购买遇到问题")
                .msg("1. 目前只支持Google Play商店购买方式, 请确定你的手机已正确安装Play Store\n" +
                        "2. 部分手机需要到应用设置里允许Google Play显示在其他应用之上")
                .positiveText(R.string.ok)
                .build().show();
    }

}
