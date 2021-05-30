package me.ghui.v2er.module.settings;

import android.content.Intent;
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
import me.ghui.v2er.general.Vtml;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.pay.PayUtil;
import me.ghui.v2er.module.pay.WXPayActivity;
import me.ghui.v2er.module.pay.WechatH5PayResultInfo;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.BaseDialog;
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
        hideLoading();
    }

    @OnClick(R.id.go_get_pro_btn)
    void onGetProClicked() {
        if (isPro()) {
            Voast.show("Pro已激活, 感谢支持");
            return;
        }

        final String msg = "1. 微信支付购买, 将绑定购买信息到你的V2EX账号当前售价为 <b>55人民币</b>.<br/>2. Google Play购买将绑定购买信息到你的Google账号当前售价为 <b>11美元</b>.<br/>3. 售价只升不降";
        new ConfirmDialog.Builder(this)
                .title("激活Pro版")
                .msg(Vtml.fromHtml(msg))
                .positiveText("微信支付", dialog -> startWXPayFlow())
                .negativeText("Google Play", dialog -> {
                    BillingManager.get().startPurchaseFlow(getActivity());
                })
                .cancelable(true)
                .build()
                .show();
    }

    private void startWXPayFlow() {
        if (!UserUtils.isLogin()) {
            Voast.show("微信支付需要您先登录v2ex账号");
            Navigator.from(getContext())
                    .to(LoginActivity.class)
                    .start();
            return;
        }

        String userName = UserUtils.getUserName();
        if (Check.isEmpty(userName)) {
            new ConfirmDialog.Builder(this)
                    .title("提示")
                    .msg("获取用户信息出错，请重新登录")
                    .positiveText("重新登录", dialog -> {
                        UserUtils.clearLogin();
                        Navigator.from(getContext())
                                .setFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .to(LoginActivity.class)
                                .start();
                    })
                    .negativeText("取消", dialog -> {
                        UserUtils.clearLogin();
                        finishToHome();
                    }).build()
                    .show();
            return;
        }

        Map<String, Object> payParams = new HashMap<>();
        payParams.put("userName", userName);
        payParams.put("version", Utils.getVersionName());
        payParams.put("os", "Android");
        String sign = PayUtil.createSign(payParams);
        payParams.put("random", sign);
        String userId = UserUtils.getUserID();
        if (Check.notEmpty(userId)) {
            payParams.put("userId", userId);
        }

        showLoading();
        APIService.get().requestWeChatH5Pay(payParams)
                .compose(rx(null))
                .subscribe(new GeneralConsumer<WechatH5PayResultInfo>() {
                    @Override
                    public void onConsume(WechatH5PayResultInfo payOrderInfo) {
                        if (payOrderInfo.getCode() == 0 && Check.isEmpty(payOrderInfo.getPayUrl())) {
                            // Alread paid
                            Voast.show("你已是付费用户");
                            UserUtils.saveIsWechatPro(true);
                            updateUI();
                            return;
                        }
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

    public void onRefreshProInfoClicked(View view) {
        // force refresh ProInfo
        if (!UserUtils.isLogin()) {
            Voast.show("请先登录");
            return;
        }

        if (UserUtils.isPro()) {
            updateUI();
            return;
        }

        new ConfirmDialog.Builder(this)
                .title("刷新激活状态")
                .msg("若你微信付费成功后, Pro状态未激活成功，可尝试刷新付费状态")
                .positiveText("刷新", dialog -> {
                    showLoading();
                    PayUtil.checkIsWechatPro(isWechatPro -> {
                        hideLoading();
                        Voast.show(isWechatPro ? "激活成功" : "激活失败");
                        updateUI();
                    });
                }).build().show();
    }

    private boolean isPro() {
        return UserUtils.isPro();
    }

}
