package com.sarmad.admobify.adsdk.banner_ads

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

abstract class BannerCallback {

    open fun onAdLoaded(adView: AdView) {

    }

    open fun onAdFailed(error: LoadAdError?) {

    }

    open fun onAdValidate() {

    }

    open fun onAdImpression() {

    }


    open fun onAdClick() {

    }

    open fun onAdClose() {

    }

    open fun onAdOpen() {

    }

    open fun onAdSwipeGestureClicked() {

    }

}