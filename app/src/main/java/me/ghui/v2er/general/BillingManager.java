package me.ghui.v2er.general;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import es.dmoral.prefs.Prefs;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ghui.toolbox.android.Check;
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
    private PurchaseListener mPurchaseListener;

    private BillingManager() {
        mBillingClient = BillingClient.newBuilder(App.get()).setListener(this).build();
    }

    public static BillingManager get() {
        return new BillingManager();
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        boolean isPro = responseCode == BillingClient.BillingResponse.OK
                && Check.notEmpty(purchases)
                && SKU_ID.equals(purchases.get(0).getSku());
        UserUtils.savePro(isPro);
        L.e("onPurchasesUpdated, isPro: " + isPro);
        if (mPurchaseListener != null) {
            mPurchaseListener.onPurchaseFinished(isPro);
        }
    }

    /**
     * 异步检查是否是付费用户
     */
    public void checkIsProAsyc() {
        L.e("checkIsProAsyc");
        checkIsProAsyc(false, null);
    }

    /**
     * 开始购买流程
     *
     * @param activity
     * @param purchaseListener
     */
    public void startPurchaseFlow(Activity activity, PurchaseListener purchaseListener, boolean toastError) {
        // check first
        checkIsProAsyc(true, isPro -> {
            if (isPro) {
                L.e("Already is Pro!");
                if (purchaseListener != null) {
                    purchaseListener.onPurchaseFinished(true);
                }
            } else {
                Runnable executeOnConnectedService = () -> {
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setType(SKU_TYPE)
                            .setSku(SKU_ID)
                            .build();
                    L.e("start Buy flow");
                    mBillingClient.launchBillingFlow(activity, billingFlowParams);
                };
                mPurchaseListener = purchaseListener;
                startServiceConnectionIfNeeded(toastError, executeOnConnectedService);
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
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                    if (billingResponse == BillingClient.BillingResponse.OK) {
                        L.e("onBillingSetupFinished");
                        runable.run();
                    } else {
                        if (toastError) {
                            Voast.show("与Google Play通信失败，请检查你的网络连接", true);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    L.e("onBillingServiceDisconnected");
                }
            });
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkIsProAsyc(boolean forch, CheckResultListener listener) {
        // 本地是Pro用户就跳过检查, 也就是只第一次做检查
        if (!forch && UserUtils.isPro()) return;
        Runnable checkTask = () -> Observable.just(mBillingClient.queryPurchases(SKU_TYPE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseConsumer<Purchase.PurchasesResult>() {
                    @Override
                    public void onConsume(Purchase.PurchasesResult result) {
                        boolean isPro = result.getResponseCode() == BillingClient.BillingResponse.OK &&
                                Check.notEmpty(result.getPurchasesList())
                                && SKU_ID.equals(result.getPurchasesList().get(0).getSku());
                        UserUtils.savePro(isPro);
                        L.e("checkIsProAsyc, isPro: " + isPro);
                        if (isPro) {
                            Prefs.with(App.get()).writeLong(LAST_CHECK_TIME, System.currentTimeMillis());
                        }
                        if (listener != null) {
                            listener.onResult(isPro);
                        }
                        mBillingClient.endConnection();
                    }
                });
        startServiceConnectionIfNeeded(false, checkTask);
    }


    public interface CheckResultListener {
        void onResult(boolean isPro);
    }

    public interface PurchaseListener {
        void onPurchaseFinished(boolean isSuccess);
    }

}
