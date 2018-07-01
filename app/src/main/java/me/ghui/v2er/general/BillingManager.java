package me.ghui.v2er.general;

import android.annotation.SuppressLint;
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
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

/**
 * 捐赠版购买管理器，提供捐赠版的购买支持、检查是否购买过高级版功能.
 */

public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";
    private final BillingClient mBillingClient;
    private static final String SKU_TYPE = BillingClient.SkuType.INAPP;
    private static final String SKU_ID = "v2er.pro";
    private static final String LAST_CHECK_TIME = Utils.KEY("last_check_time");
    private PurchaseListener mPurchaseListener;


    public static BillingManager get() {
        return new BillingManager();
    }

    private BillingManager() {
        mBillingClient = BillingClient.newBuilder(App.get()).setListener(this).build();
    }

    public void destroy() {
        mBillingClient.endConnection();
    }

    private void startServiceConnectionIfNeeded(final Runnable runable) {
        if (mBillingClient.isReady()) {
            if (runable != null) {
                runable.run();
            }
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                    if (billingResponse == BillingClient.BillingResponse.OK) {
                        if (runable != null) {
                            runable.run();
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }

    public void startPurchaseFlow(Activity activity, PurchaseListener purchaseListener) {
        // check first
        checkIsProAsyc(true, isPro -> {
            if (isPro) {
                if (purchaseListener != null) {
                    purchaseListener.onPurchaseFinished(true);
                }
            } else {
                Runnable executeOnConnectedService = () -> {
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setType(SKU_TYPE)
                            .setSku(SKU_ID)
                            .build();
                    mBillingClient.launchBillingFlow(activity, billingFlowParams);
                };
                mPurchaseListener = purchaseListener;
                startServiceConnectionIfNeeded(executeOnConnectedService);
            }
        });
    }

    public void checkIsProAsyc() {
        checkIsProAsyc(false, null);
    }


    @SuppressLint("CheckResult")
    public void checkIsProAsyc(boolean forch, CheckResultListener listener) {
        // 本地是Pro用户就跳过检查, 也就是只第一次做检查
        if (!forch && UserUtils.isPro()) return;
        startServiceConnectionIfNeeded(() ->
                Observable.just(mBillingClient.queryPurchases(SKU_TYPE))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                                    if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                                        if (Check.notEmpty(result.getPurchasesList())
                                                && SKU_ID.equals(result.getPurchasesList().get(0).getSku())) {
                                            UserUtils.savePro(true);
                                            if (listener != null) {
                                                listener.onResult(true);
                                            }
                                        } else {
                                            UserUtils.savePro(false);
                                        }
                                    }
                                    Prefs.with(App.get()).writeLong(LAST_CHECK_TIME, System.currentTimeMillis());
                                    if (listener != null) {
                                        listener.onResult(false);
                                    }
                                    destroy();
                                }
                        ));
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        boolean isPro;
        if (responseCode == BillingClient.BillingResponse.OK
                && Check.notEmpty(purchases)
                && SKU_ID.equals(purchases.get(0).getSku())) {
            isPro = true;
        } else {
            isPro = false;
        }
        UserUtils.savePro(isPro);
        if (mPurchaseListener != null) {
            mPurchaseListener.onPurchaseFinished(isPro);
        }
    }

    public interface CheckResultListener {
        void onResult(boolean isPro);
    }

    public interface PurchaseListener {
        void onPurchaseFinished(boolean isSuccess);
    }

}
