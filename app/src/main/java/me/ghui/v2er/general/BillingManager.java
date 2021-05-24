package me.ghui.v2er.general;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.prefs.Prefs;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ghui.v2er.bus.Bus;
import me.ghui.v2er.bus.event.PayResultEvent;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.network.BaseConsumer;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

/**
 * 捐赠版购买管理器，提供捐赠版的购买支持、检查是否购买过高级版功能.
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String SKU_TYPE = BillingClient.SkuType.INAPP;
    private static final String SKU_ID = "v2er.pro";
    private static final String LAST_CHECK_TIME = Utils.KEY("last_check_time");
    private final BillingClient mBillingClient;

    private BillingManager() {
        mBillingClient = BillingClient.newBuilder(App.get())
                .enablePendingPurchases()
                .setListener(this).build();
    }

    public static BillingManager get() {
        return new BillingManager();
    }

    /**
     * 异步检查是否是付费用户
     */
    public void checkIsGoogleProAsyc(boolean forceCheck) {
        L.e("checkIsProAsyc");
        checkIsGoogleProAsyc(forceCheck, null);
    }

    /**
     * 开始购买流程
     *
     * @param activity
     */
    public void startPurchaseFlow(Activity activity) {
        // check first
        checkIsGoogleProAsyc(true, isPro -> {
            if (isPro) {
                L.e("Already is Pro!");
                Voast.show("你已是高级用户");
                return;
            }
            startFetchSkuDetails((billingResult, skuDetails) -> {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Voast.show("拉取Google Play商品信息失败, 请重试");
                    return;
                }
                SkuDetails skuDetail = skuDetails.get(0);
                L.e("SkuDetails: " + skuDetail.toString());
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetail)
                        .build();
                L.e("start Buy flow");
                mBillingClient.launchBillingFlow(activity, billingFlowParams);
            });
        });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        boolean isPro = billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && Check.notEmpty(purchases)
                && SKU_ID.equals(purchases.get(0).getSku());
        UserUtils.saveIsGooglePro(isPro);
        L.e("onPurchasesUpdated, isPro: " + isPro);
        PayResultEvent payResultEvent = new PayResultEvent(isPro,
                PayResultEvent.PayWay.GOOGLE_PLAY_PAY,
                null);
        Bus.post(payResultEvent);
    }

    private void startFetchSkuDetails(SkuDetailsResponseListener skuDetailsResponseListener) {
        startServiceConnectionIfNeeded(true, new Runnable() {
            @Override
            public void run() {
                List<String> skuList = new ArrayList<>();
                skuList.add(SKU_ID);
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.INAPP)
                        .build();
                mBillingClient.querySkuDetailsAsync(skuDetailsParams, skuDetailsResponseListener);
            }
        });
    }

    private void startServiceConnectionIfNeeded(boolean toastError, final Runnable runable) {
        if (runable == null) {
            L.e("runnable is null, return");
            return;
        }
        if (mBillingClient.isReady()) {
            L.e("BillClient is Ready");
            runable.run();
        } else {
            L.e("BillClient doesn't ready, startConnection");
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        L.e("onBillingSetupFinished");
                        new Handler(Looper.getMainLooper())
                                .post(runable);
                    } else {
                        if (toastError) {
                            Voast.show("与Google Play通信失败，请检查你的网络连接", true);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    L.e("onBillingServiceDisconnected");
//                    Voast.show("与Google Play断开连接, 请重试");
                }
            });
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void checkIsGoogleProAsyc(boolean forch, CheckResultListener listener) {
        // 本地是Pro用户就跳过检查, 也就是只第一次做检查
        if (!forch && UserUtils.isPro()) return;
        Runnable checkTask = () -> Observable.just(mBillingClient.queryPurchases(SKU_TYPE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseConsumer<Purchase.PurchasesResult>() {
                    @Override
                    public void onConsume(Purchase.PurchasesResult result) {
                        boolean isPro = result.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                                Check.notEmpty(result.getPurchasesList())
                                && SKU_ID.equals(result.getPurchasesList().get(0).getSku());
                        UserUtils.saveIsGooglePro(isPro);
                        L.e("checkIsGooglePro: " + isPro);
                        if (isPro) {
                            Prefs.with(App.get()).writeLong(LAST_CHECK_TIME, System.currentTimeMillis());
                        }
                        if (listener != null) {
                            listener.onResult(isPro);
                        }
                    }
                });
        startServiceConnectionIfNeeded(false, checkTask);
    }

    public interface CheckResultListener {
        void onResult(boolean isPro);
    }

}
