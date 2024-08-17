package com.sarmad.admobify.adsdk.billing

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

abstract class BillingListener {

    abstract fun purchasedAndSubscribedList(purchaseList:List<Purchase>, subList:List<Purchase>)

    abstract fun productAndSubMetaData(products:List<ProductDetails>, subscriptions:List<ProductDetails>)

    abstract fun purchasedORSubDone(productsList:List<Purchase?>)

}