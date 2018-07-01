package me.ghui.v2er.module.settings;

import android.graphics.Color;
import android.view.Window;
import android.widget.TextView;

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
    TextView mProTextTitle;

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
        mProTextTitle = findViewById(R.id.pro_title_tv);
        isPro = UserUtils.isPro();
        mProTextTitle.setText(isPro? "V2er Pro『已激活』" : "V2er Pro");
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

    @OnClick(R.id.old_pro_user_btn)
    void onOldUserClicked() {
        new ConfirmDialog.Builder(this)
                .title("V2er APP合并计划")
                .msg("V2er和V2erPro已合并为同一个应用，若你是原付费用户，请向我发邮件索取新的内购兑换码" + "给您带来的不便我深感抱歉")
                .positiveText("去获取", dialog -> Utils.sendMigrateMail(this))
                .negativeText("暂不")
                .build().show();
    }

    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        if(isPro){
            Voast.show("Pro已激活，感谢支持!");
            return;
        }
        BillingManager.get().startPurchaseFlow(getActivity(), isSuccess -> {
            isPro = isSuccess;
            String msg = isSuccess?"激活成功!" : "激活失败";
            Voast.show(msg);
        });
    }

}
