package com.sarmad.admobify.adsdk.billing

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingUtils private constructor(private val builder: Builder) {

    private var billingClient: BillingClient? = null

    private var activity = builder.activity

    private val billingListener = builder.billingListener

    companion object {
        @JvmStatic
        fun getInstance(activity: Activity): Builder {
            return Builder()
        }
    }

    init {
        initBilling()
    }


    private fun initBilling() {
        billingClient = BillingClient.newBuilder(activity ?: return)
            .setListener { billingResult, purchases ->
                //this callback invoked when purchase or subscribe done
                if (billingResult.responseCode == BillingResponseCode.OK && purchases?.isNotEmpty() == true) {
                    val tempPurchaseList = ArrayList<Purchase?>(purchases)

                    CoroutineScope(Dispatchers.IO).launch{
                        tempPurchaseList.forEach {
                            handlePurchase(it)
                        }
                    }

                    billingListener?.purchasedORSubscribed(purchases)
                } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                }
            }.build()

        startConnection()
    }


    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    queryAvailableProducts()
                }
            }

            override fun onBillingServiceDisconnected() {

            }
        })
    }

    fun oneTimePurchase(purchaseKey: String) {
        billingClient?.queryProductDetailsAsync(
            queryDetailParams(
                ProductType.INAPP,
                purchaseKey
            )
        ) { result, products ->
            if (result.responseCode == BillingResponseCode.OK && products.isNotEmpty()) {
                launchBillingFlow(products[0])
            }
        }
    }


    fun subscribe(subKey: String) {
        billingClient?.queryProductDetailsAsync(
            queryDetailParams(
                ProductType.SUBS,
                subKey
            )
        ) { result, products ->
            if (result.responseCode == BillingResponseCode.OK && products.isNotEmpty()) {
                launchBillingFlow(products[0])
            }
        }
    }


    private fun queryAvailableProducts() {

        val productList = ArrayList<QueryProductDetailsParams.Product>()

        builder.getPurchaseIds.forEach {
            productList.add(queryProductBuilder(ProductType.INAPP, it))
        }

        builder.getSubsIds.forEach {
            productList.add(queryProductBuilder(ProductType.SUBS, it))
        }

        val param = QueryProductDetailsParams.newBuilder()
            .setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(
            param
        ) { result: BillingResult, products: List<ProductDetails> ->
            if (result.responseCode == BillingResponseCode.OK) {
                billingListener?.purchaseAndSubList(products)
            }
        }
    }


    private fun launchBillingFlow(productDetails: ProductDetails) {

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams =
            BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                .build()

        billingClient?.launchBillingFlow(activity ?: return, billingFlowParams)

    }

    private fun handlePurchase(purchase: Purchase?) {
        if (purchase?.purchaseState == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {

                val acknowledgeParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()


                billingClient?.acknowledgePurchase(acknowledgeParams) { _ -> }

            }
        }
    }

    private fun queryDetailParams(type: String, id: String): QueryProductDetailsParams {
        return QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(queryProductBuilder(type, id))).build()
    }

    private fun queryProductBuilder(type: String, id: String): QueryProductDetailsParams.Product {
        return QueryProductDetailsParams.Product.newBuilder()
            .setProductType(type).setProductId(id).build()
    }


    /** Billing Module Builder */

    class Builder {

        private val purchaseIDS = HashSet<String>()

        private val subscriptionIDS = HashSet<String>()

        private var mBillingListener: BillingListener? = null


        var activity: Activity? = null


        val getPurchaseIds: Set<String> get() = purchaseIDS

        val getSubsIds: Set<String> get() = subscriptionIDS


        val billingListener: BillingListener? get() = mBillingListener

        fun setPurchaseIds(vararg purchaseIds: String) {
            purchaseIDS.clear()
            purchaseIds.forEach {
                purchaseIDS.add(it)
            }
        }


        fun setSubscriptionIds(vararg subscriptionIds: String) {
            subscriptionIDS.clear()
            subscriptionIds.forEach {
                subscriptionIDS.add(it)
            }
        }

        fun setBillingListener(billingListener: BillingListener) {
            mBillingListener = billingListener
        }

        fun build(activity: Activity): BillingUtils {
            this.activity = activity
            return BillingUtils(this)
        }

    }


}