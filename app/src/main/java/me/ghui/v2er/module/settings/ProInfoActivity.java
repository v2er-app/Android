package me.ghui.v2er.module.settings;

import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.bus.event.PayResultEvent;
import me.ghui.v2er.general.BillingManager;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.pay.WXPayActivity;
import me.ghui.v2er.module.pay.WechatH5PayResultInfo;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
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

    @Override
    protected int attachLayoutRes() {
        return R.layout.pro_page;
    }

    @Override
    protected void init() {
        super.init();
        updateUI();
    }

    private void updateUI() {
        mBuyButton.setText(isPro() ? "Pro已激活, 感谢支持" : "去激活");
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this).reload();
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(mRootView);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayResult(PayResultEvent payResultEvent) {
        String msg = payResultEvent.isSuccess ? "购买成功" : "购买失败";
        Voast.show(msg);
        updateUI();
    }

    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        if (isPro()) {
            Voast.show("Pro已激活, 感谢支持");
            return;
        }
        new ConfirmDialog.Builder(this)
                .title("激活Pro版")
                .msg("微信支付购买, 将绑定购买信息到你的V2EX账号. \n" +
                        "Google Play购买将绑定购买信息到你的Google账号.")
                .positiveText(UserUtils.isLogin() ? "微信支付" : "去登录", dialog -> {
                    startWXPayFlow();
                }).negativeText("Google Play", dialog -> {
                   BillingManager.get().startPurchaseFlow(getActivity());
                })
                .build()
                .show();
    }

    private void startWXPayFlow() {
        if (!UserUtils.isLogin()) {
            Navigator.from(getContext())
                    .to(LoginActivity.class)
                    .start();
            return;
        }

        Map<String, Object> payParams = new HashMap<>();
        payParams.put("userName", UserUtils.getUserName());
        payParams.put("userId", UserUtils.getUserID());
        APIService.get().requestWeChatH5Pay(payParams)
                .compose(rx())
                .subscribe(new GeneralConsumer<WechatH5PayResultInfo>() {
                    @Override
                    public void onConsume(WechatH5PayResultInfo payOrderInfo) {
                        WXPayActivity.startPay(payOrderInfo.getPayUrl(), payOrderInfo.getOrderId(), getContext());
                    }
                });
    }

    public void onNoResponseClicked(View view) {
        new ConfirmDialog.Builder(this)
                .title("购买遇到问题")
                .msg("1. Google Play商店购买方式, 请确定你的手机已正确安装Play Store\n" +
                        "2. 部分手机需要到应用设置里允许Google Play显示在其他应用之上\n" +
                        "3. 其它问题请发邮件到v2er.app@outlook.com")
                .positiveText(R.string.ok)
                .build().show();
    }

    private boolean isPro() {
        return UserUtils.isPro();
    }

}
