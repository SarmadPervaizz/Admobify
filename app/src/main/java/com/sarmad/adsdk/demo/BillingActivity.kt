package com.sarmad.adsdk.demo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.sarmad.admobify.adsdk.billing.BillingListener
import com.sarmad.admobify.adsdk.billing.BillingUtils

class BillingActivity : AppCompatActivity() {

    var billingUtils: BillingUtils? = null
    var purchaseKey = "photo_editor1"
    var subKey = "photo_editor_monthly"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)
        val btnPurchase = findViewById<Button>(R.id.btnPurchase)

        val btnSubscribe = findViewById<Button>(R.id.btnSubscribe)


        billingUtils = BillingUtils.getInstance().setPurchaseIds(purchaseKey).
        setSubscriptionIds(subKey).setBillingListener(object : BillingListener() {

            /** Products user currently subscribed or purchased **/
            override fun purchasedAndSubscribedList(
                purchaseList: List<Purchase>,
                subList: List<Purchase>) {}

            /** Products and Subscriptions meta data like price, duration etc */
            override fun productAndSubMetaData(
                products: List<ProductDetails>,
                subscriptions: List<ProductDetails>) {}

            /** this callback invoked when purchase or subscription
             * transaction successful on runtime */
            override fun purchasedORSubDone(productsList: List<Purchase?>) {}

        }).build(this)

        btnPurchase.setOnClickListener {
            billingUtils?.oneTimePurchase(purchaseKey)
        }

        btnSubscribe.setOnClickListener {
            billingUtils?.subscribe(subKey)
        }

    }


}