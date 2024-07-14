package com.sarmad.admobify.adsdk.native_ads

import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

abstract class NativeAdCallback {

    open fun adLoaded() {

    }

    open fun adLoaded(nativeAd: NativeAd?) {

    }

    open fun adFailed(error: LoadAdError?) {

    }

    open fun adImpression() {

    }

    open fun adClicked() {

    }

    open fun adValidate() {

    }


}