package me.ghui.v2er.module.settings;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.BillingManager;
import me.ghui.v2er.general.ColorModeReloader;
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
    @BindView(R.id.go_get_pro_btn)
    TextView mBuyButton;
    private boolean isPro;

    @Override
    protected int attachLayoutRes() {
        return R.layout.pro_page;
    }

    @Override
    protected void init() {
        super.init();
        isPro = UserUtils.isPro();
        updateUI();
    }

    private void updateUI() {
        mBuyButton.setText(isPro ? "Pro已激活, 感谢支持" : "去支持");
    }

    @Override
    protected void reloadMode(int mode) {
        ColorModeReloader.target(this).reload();
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(mRootView);
    }

    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        if (isPro) {
            Voast.show("Pro已激活, 感谢支持");
            return;
        }
        BillingManager.get().startPurchaseFlow(getActivity(), isSuccess -> {
            isPro = isSuccess;
            String msg = isSuccess ? "激活成功!" : "激活失败";
            Voast.show(msg);
            updateUI();
        }, true);
    }

    public void onNoResponseClicked(View view) {
        new ConfirmDialog.Builder(this)
                .title("购买遇到问题")
                .msg("1. 目前只支持Google Play商店购买方式, 请确定你的手机已正确安装Play Store\n" +
                        "2. 部分手机需要到应用设置里允许Google Play显示在其他应用之上\n3. 其它问题可直接联系开发者微信: ghuiii")
                .positiveText(R.string.ok)
                .build().show();
    }

}
