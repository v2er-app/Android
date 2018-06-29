package me.ghui.v2er.general;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import es.dmoral.prefs.Prefs;
import io.reactivex.Observable;
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

    public BillingManager() {
        mBillingClient = BillingClient.newBuilder(App.get()).setListener(this).build();
//        startServiceConnectionIfNeeded(null);
    }

    public void destroy() {
        mBillingClient.endConnection();
    }

    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        if (mBillingClient.isReady()) {
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                    if (billingResponse == BillingClient.BillingResponse.OK) {
                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(TAG, "onBillingServiceDisconnected()");
                }
            });
        }
    }

    public void startPurchaseFlow( Activity activity) {
        Runnable executeOnConnectedService = () -> {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setType(SKU_TYPE)
                    .setSku(SKU_ID)
                    .build();
            mBillingClient.launchBillingFlow(activity, billingFlowParams);
        };
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public void checkIsProAsyc(){
        checkIsProAsyc(false);
    }

    @SuppressLint("CheckResult")
    public void checkIsProAsyc(boolean forch){
        // 本地是Pro用户就跳过检查, 也就是只第一次做检查
        if (!forch) {
            if (UserUtils.isPro()) {
                Log.d(TAG, "isLocalPro , pass check...");
                return;
            }
        }
        startServiceConnectionIfNeeded(() ->
           Observable.just(mBillingClient.queryPurchases(SKU_TYPE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.getResponseCode() == BillingClient.BillingResponse.OK
                        && Check.notEmpty(result.getPurchasesList())) {
                        if(SKU_ID.equals(result.getPurchasesList().get(0).getSku())) {
                            UserUtils.savePro(true);
                            Log.d(TAG, "saved pro when check...");
                        }
                     }
                     Prefs.with(App.get()).writeLong(LAST_CHECK_TIME, System.currentTimeMillis());
                     destroy();
                }
            ));
        }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated: ");
        if(responseCode == BillingClient.BillingResponse.OK
                && Check.notEmpty(purchases)
                && SKU_ID.equals(purchases.get(0).getSku())){
            Log.d(TAG, "onPurchasesUpdated: OK!!!");
            UserUtils.savePro(true);
        } else {
            UserUtils.savePro(false);
        }
    }

}
