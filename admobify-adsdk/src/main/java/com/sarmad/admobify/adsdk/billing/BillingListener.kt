package com.sarmad.admobify.adsdk.billing

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

abstract class BillingListener {

    abstract fun purchaseAndSubList(productsList:List<ProductDetails>)

    abstract fun purchasedORSubscribed(productsList:List<Purchase?>)

}