package com.sarmad.admobify.adsdk.utils

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Admobify {


    /** Throw exceptions during ad validation or Invalid ad ids */
    private var canThrowExceptions = false

    internal fun canThrowException() = canThrowExceptions


    /** To stop Showing Ads if User Purchased App */

    private var premiumUser = false

    fun isPremiumUser() = premiumUser

    fun setPremiumUser(value: Boolean) {
        premiumUser = value
    }


    /** Initialize Ads Sdk */
    fun initialize(
        context: Context,
        testDevicesList: ArrayList<String>,
        premiumUser: Boolean,
        canThrowException: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            try {

                FirebaseApp.initializeApp(context.applicationContext)

                canThrowExceptions = canThrowException

                setPremiumUser(premiumUser)

                MobileAds.initialize(context.applicationContext)

                MobileAds.setRequestConfiguration(
                    RequestConfiguration.Builder().setTestDeviceIds(testDevicesList).build()
                )

            } catch (ignored: Exception) { }

        }


    }


}