package me.ghui.v2er.general;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import me.ghui.toolbox.android.Check;

/**
 * 捐赠版购买管理器，提供捐赠版的购买支持、检查是否购买过高级版功能.
 */

public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";
    private final BillingClient mBillingClient;
    private static final String SKU_TYPE = BillingClient.SkuType.INAPP;
    private static final String SKU_ID = "v2er.pro";

    public BillingManager() {
        mBillingClient = BillingClient.newBuilder(App.get()).setListener(this).build();
        startServiceConnectionIfNeeded(null);
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

    public void destroy() {
        mBillingClient.endConnection();
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated: ");
        if(responseCode == BillingClient.BillingResponse.OK
                && Check.notEmpty(purchases)
                && SKU_ID.equals(purchases.get(0).getSku())){
            Log.d(TAG, "onPurchasesUpdated: OK!!!");
        }
    }

}
