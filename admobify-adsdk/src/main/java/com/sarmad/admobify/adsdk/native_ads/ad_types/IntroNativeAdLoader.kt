package com.sarmad.admobify.adsdk.native_ads.ad_types

import android.app.Application
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.sarmad.admobify.adsdk.native_ads.NativeAdCallback
import com.sarmad.admobify.adsdk.utils.Logger

internal object IntroNativeAdLoader {

    const val TAG = "IntroNativeAdLoader"

    private var nativeAdCallback: NativeAdCallback? = null

    private var nativeAd: NativeAd? = null

    private var loadingNativeAd = false


    fun loadNativeAd(
        application: Application,
        adId: String,
        adListener: NativeAdCallback
    ) {

        nativeAdCallback = adListener

        if (nativeAd != null) {

            nativeAdCallback?.adLoaded(nativeAd)

            Logger.logDebug(TAG, "loadNativeAd:adAlreadyPreCached intro ad")

        } else {

            if (loadingNativeAd) {
                Logger.logDebug(TAG, "loadNativeAd:Already loading intro ad")
                return
            }

            loadingNativeAd = true

            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val nativeOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

            val adLoader = AdLoader.Builder(application, adId).withNativeAdOptions(nativeOptions).forNativeAd { newNativeAd ->

               nativeAd = newNativeAd

            }.withAdListener(attachAdListener()).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

    }

    private fun attachAdListener(): AdListener {
        val listener = object : AdListener() {

            override fun onAdClicked() {
                nativeAdCallback?.adClicked()
                Logger.logDebug(TAG, "loadNativeAd:onAdClicked")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loadingNativeAd = false
                nativeAdCallback?.adFailed(error)
                Logger.logDebug(TAG, "loadNativeAd:onAdFailedToLoad:${error.message}")
            }

            override fun onAdImpression() {
                nativeAd = null
                nativeAdCallback?.adImpression()
                Logger.logDebug(TAG, "loadNativeAd:onAdImpression")
            }

            override fun onAdLoaded() {
                loadingNativeAd = false
                nativeAdCallback?.adLoaded(nativeAd)
                Logger.logDebug(TAG, "loadNativeAd:onAdLoaded")
            }

        }

        return listener
    }


}