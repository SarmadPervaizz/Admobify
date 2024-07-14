package com.sarmad.admobify.adsdk.utils

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.android.gms.ads.RequestConfiguration;

class Admobify {

    companion object {


        /** Throw exceptions during ad validation or Invalid ad ids */
        private var canThrowExceptions = false

        internal fun canThrowException() = canThrowExceptions


        /** Helpful for Ad validation and Logging errors
         * to check whether project is in debug or prod mode */
        internal var mBuildFlavor = "appDev"

        internal var mBuildVariant = "debug"



        /** To Avoid Showing Ads if User Purchased App */

        private var premiumUser = false

        fun isPremiumUser() = premiumUser

        fun setPremiumUser(value:Boolean){
            premiumUser = value
        }


        /** Initialize Ads Sdk */
        fun initialize(
            context: Context,
            testDevicesList:ArrayList<String>,
            premiumUser:Boolean,
            buildFlavor: String,
            buildVariant: String,
            canThrowException: Boolean = false
        ) {
            FirebaseApp.initializeApp(context.applicationContext)
            canThrowExceptions = canThrowException
            mBuildFlavor = buildFlavor
            mBuildVariant = buildVariant
            setPremiumUser(premiumUser)
            MobileAds.initialize(context.applicationContext)
//            val reqConfig = RequestConfiguration().toBuilder().setTestDeviceIds(testDevicesList).build()
            MobileAds.setRequestConfiguration(RequestConfiguration.Builder().setTestDeviceIds(testDevicesList).build())
        }

    }


}