package com.sarmad.admobify.adsdk.interstitial_ads

import com.google.android.gms.ads.LoadAdError

abstract class InterAdLoadCallback {

    open fun adAlreadyLoaded() {

    }

    open fun adLoaded() {

    }

    open fun adFailed(error: LoadAdError?,msg:String?) {

    }

    open fun adValidate(){

    }

}