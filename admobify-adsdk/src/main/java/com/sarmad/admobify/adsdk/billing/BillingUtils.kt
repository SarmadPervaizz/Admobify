package com.sarmad.admobify.adsdk.billing

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val LOG_TAG = "BillingUtils"

class BillingUtils private constructor(private val builder: Builder) {

    private var billingClient: BillingClient? = null

    private var activity = builder.activity

    private val billingListener = builder.billingListener

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        @JvmStatic
        fun getInstance(): Builder {
            return Builder()
        }
    }

    init {
        initBilling()
    }




    private fun initBilling() {
        try {

            val pendingPurchaseParams =
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()

            billingClient = BillingClient.newBuilder(activity ?: return)
                .enablePendingPurchases(pendingPurchaseParams)
                .setListener { billingResult, purchases ->
                        //this callback invoked when purchase or subscribe done

                        if (billingResult.responseCode == BillingResponseCode.OK
                            && purchases?.isNotEmpty() == true) {

                            val tempPurchaseList = ArrayList<Purchase?>(purchases)

                            CoroutineScope(Dispatchers.IO).launch {
                                tempPurchaseList.forEach {
                                    handlePurchase(it)
                                }
                            }

                            Logger.log(Level.DEBUG,Category.Billing, "purchase or subscription done")

                            handler.post {
                                billingListener?.purchasedORSubDone(purchases)
                            }

                        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                            Logger.log(Level.DEBUG,Category.Billing, "purchase cancelled by user")
                        } else {
                            Logger.log(Level.ERROR,Category.Billing, "unknown response code:${billingResult.responseCode}" +
                                        "msg:${billingResult.debugMessage}"
                            )
                        }

                }.build()

            startConnection()
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "Exception initializing:${e.message}")
        }
    }


    private fun startConnection() {
        try {
            billingClient?.startConnection(object : BillingClientStateListener {

                override fun onBillingSetupFinished(billingResult: BillingResult) {

                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        Logger.log(Level.DEBUG,Category.Billing, "billing setup finished response okay")
                        CoroutineScope(Dispatchers.IO).launch {
                            queryProductsAndSubDetails()
                            queryPurchasedAndSubs()
                        }
                    } else {
                        Logger.log(Level.DEBUG,Category.Billing,
                            "billing setup finished response:${billingResult.responseCode}" +
                                    " msg:${billingResult.debugMessage}"
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Logger.log(Level.DEBUG,Category.Billing, "billing service disconnected")
                }
            })
        } catch (ex: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception starting connection:${ex.message}")
        }
    }

    fun oneTimePurchase(purchaseKey: String) {
        try {
            Logger.log(Level.DEBUG,Category.Billing, "one time purchase called")
            billingClient?.queryProductDetailsAsync(
                queryDetailParams(
                    ProductType.INAPP,
                    purchaseKey
                )
            ) { result, products ->
                if (result.responseCode == BillingResponseCode.OK && products.isNotEmpty()) {
                    Logger.log(Level.DEBUG,Category.Billing, "launching billing flow for one time purchase")
                    launchBillingFlow(products[0], false)
                } else {
                    Logger.log(Level.DEBUG,Category.Billing,
                        "can't launch billing flow for purchase response:${result.responseCode} msg:${result.debugMessage}"
                    )
                }
            }
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception performing one time purchase:${e.message}")
        }
    }


    fun subscribe(subKey: String) {
        try {
            Logger.log(Level.DEBUG,Category.Billing, "subscribe called")

            billingClient?.queryProductDetailsAsync(
                queryDetailParams(
                    ProductType.SUBS,
                    subKey
                )
            ) { result, products ->
                if (result.responseCode == BillingResponseCode.OK && products.isNotEmpty()) {
                    Logger.log(Level.DEBUG,Category.Billing, "launching billing flow for subscription")
                    Logger.log(Level.DEBUG,Category.Billing, "sub products:$products")
                    launchBillingFlow(products[0], true)
                } else {
                    Logger.log(Level.DEBUG,Category.Billing,
                        "can't launch billing flow for subscription response:${result.responseCode} msg:${result.debugMessage}"
                    )
                }
            }
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception performing subscribe:${e.message}")
        }
    }


    private fun queryProductsAndSubDetails() {

        querySubscriptionsDetails(subscriptions = { subs->

            queryProductsDetails(purchases = { purchases->
                handler.post {
                    billingListener?.productAndSubMetaData(purchases,subs)
                }
            })

        })
    }

    private fun querySubscriptionsDetails(subscriptions: (List<ProductDetails>) -> Unit) {
        try {
            val subList = ArrayList<QueryProductDetailsParams.Product>()

            builder.getSubsIds.forEach {
                subList.add(queryProductBuilder(ProductType.SUBS, it))
            }

            val param = QueryProductDetailsParams.newBuilder()
                .setProductList(subList).build()

            billingClient?.queryProductDetailsAsync(
                param
            ) { result: BillingResult, products: List<ProductDetails> ->
                if (result.responseCode == BillingResponseCode.OK) {
                    Logger.log(Level.DEBUG,Category.Billing, "subscriptions:$products")
                    subscriptions.invoke(products)
                } else {
                    subscriptions.invoke(emptyList())
                }
            }
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception querying subscriptions details:${e.message}")
            subscriptions.invoke(emptyList())
        }
    }


    private fun queryProductsDetails(purchases: (List<ProductDetails>) -> Unit) {
        try {
            Logger.log(Level.DEBUG,Category.Billing, "querying products details")

            val productList = ArrayList<QueryProductDetailsParams.Product>()

            builder.getPurchaseIds.forEach {
                productList.add(queryProductBuilder(ProductType.INAPP, it))
            }

            val param = QueryProductDetailsParams.newBuilder()
                .setProductList(productList).build()

            billingClient?.queryProductDetailsAsync(
                param
            ) { result: BillingResult, products: List<ProductDetails> ->
                if (result.responseCode == BillingResponseCode.OK) {
                    Logger.log(Level.DEBUG,Category.Billing, "products:$products")
                    purchases.invoke(products)
                } else {
                    purchases.invoke(emptyList())
                }
            }
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception querying products details:${e.message}")
            purchases.invoke(emptyList())
        }
    }


    fun queryPurchasedAndSubs(){
        queryPurchases(purchases = {purchases->
            querySubscriptions {subscriptions->
                handler.post {
                    billingListener?.purchasedAndSubscribedList(purchases, subList = subscriptions)
                }
            }
        })
    }

    fun querySubscriptions(subscriptions:(List<Purchase>)->Unit) {
        try {

            val params = QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

            billingClient?.queryPurchasesAsync(params) { billingResult, subscriptionsList ->

                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Logger.log(Level.DEBUG,Category.Billing, "subscribed products:$subscriptionsList")
                    subscriptions.invoke(subscriptionsList)
                } else {
                    Logger.log(Level.DEBUG,Category.Billing, "query subscribed products response unknown:${billingResult.responseCode}")
                    subscriptions.invoke(subscriptionsList)
                }
            }
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception querying subscribed products:${e.message}")
            subscriptions.invoke(emptyList())
        }
    }

    fun queryPurchases(purchases:(List<Purchase>)->Unit){
        try{
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.INAPP)
                .build()

            billingClient?.queryPurchasesAsync(params) { billingResult, purchasesList ->

                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Logger.log(Level.DEBUG,Category.Billing, "purchased products:$purchasesList")
                    purchases.invoke(purchasesList)
                } else {
                    Logger.log(Level.DEBUG,Category.Billing, "query purchase products response unknown:${billingResult.responseCode}")
                    purchases.invoke(purchasesList)
                }
            }
        } catch (e:Exception){
            Logger.log(Level.ERROR,Category.Billing, "exception querying purchased products:${e.message}")
            purchases.invoke(emptyList())
        }

    }



     fun consumePurchase() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()

        billingClient?.queryPurchasesAsync(params) { billingResult, purchasesList ->

            if (billingResult.responseCode == BillingResponseCode.OK) {
                consumeToken(purchasesList[0].purchaseToken)
            } else {
                // Handle error
            }
        }
    }

     private fun consumeToken(token:String){
        val params = ConsumeParams.newBuilder().setPurchaseToken(token).build()
        billingClient?.consumeAsync(params,object:ConsumeResponseListener{
            override fun onConsumeResponse(result: BillingResult, msg: String) {
                Logger.log(Level.DEBUG,Category.Billing,"consume response:${result.responseCode} msg:$msg")
            }
        })
    }

    private fun launchBillingFlow(productDetails: ProductDetails, sub: Boolean) {
        try {
            val subOfferDetails = if (sub) productDetails.subscriptionOfferDetails else null
            val productDetailsParamsList =
                if (sub && subOfferDetails?.isNotEmpty() == true) {
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(subOfferDetails[0].offerToken)
                            .build()
                    )
                } else {
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                }

            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .build()

            billingClient?.launchBillingFlow(activity ?: return, billingFlowParams)
        } catch (e: Exception) {
            Logger.log(Level.ERROR,Category.Billing, "exception launching billing flow:${e.message}")
        }
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


        internal var activity: Activity? = null


        internal val getPurchaseIds: Set<String> get() = purchaseIDS

        internal val getSubsIds: Set<String> get() = subscriptionIDS


        internal val billingListener: BillingListener? get() = mBillingListener

        fun setPurchaseIds(vararg purchaseIds: String): Builder {
            purchaseIDS.clear()
            purchaseIds.forEach {
                purchaseIDS.add(it)
            }
            return this
        }


        fun setSubscriptionIds(vararg subscriptionIds: String): Builder {
            subscriptionIDS.clear()
            subscriptionIds.forEach {
                subscriptionIDS.add(it)
            }
            return this
        }

        fun setBillingListener(billingListener: BillingListener): Builder {
            mBillingListener = billingListener
            return this
        }

        fun build(activity: Activity): BillingUtils {
            this.activity = activity
            return BillingUtils(this)
        }

    }


}