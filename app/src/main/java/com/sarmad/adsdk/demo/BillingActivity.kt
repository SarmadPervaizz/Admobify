package com.sarmad.adsdk.demo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
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
        billingUtils = BillingUtils.getInstance().setPurchaseIds(purchaseKey).
        setSubscriptionIds(subKey).setBillingListener(object : BillingListener() {

            override fun productAndSubMetaData(
                purchaseList: List<ProductDetails>,
                subList: List<ProductDetails>
            ) {

            }

            override fun purchasedAndSubscribedList(
                purchaseList: List<Purchase>,
                subList: List<Purchase>
            ) {

            }

            override fun purchasedORSubDone(productsList: List<Purchase?>) {
                Toast.makeText(this@BillingActivity, "purchasedORSubDone", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@BillingActivity,BannerAdsActivity::class.java))
                }
            }).build(this)

        val purchase = findViewById<Button>(R.id.btnPurchase)

        val subscription = findViewById<Button>(R.id.btnSubscribe)

        purchase.setOnClickListener {
            billingUtils?.oneTimePurchase(purchaseKey)
        }

        subscription.setOnClickListener {
            billingUtils?.subscribe(subKey)
        }

    }


}