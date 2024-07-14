package com.sarmad.admobify.adsdk.native_ads.ad_types

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.sarmad.admobify.adsdk.native_ads.NativeAdCallback
import com.sarmad.admobify.adsdk.utils.Logger

internal object ExitNativeAdLoader {

    const val TAG = "ExitNativeAdLoader"

    private var nativeAdCallback: NativeAdCallback? = null

    private var nativeAd: NativeAd? = null

    private var loadingNativeAd = false


    fun loadNativeAd(
        context: Context,
        adId: String,
        adListener: NativeAdCallback
    ) {

        nativeAdCallback = adListener

        if (nativeAd != null) {

            nativeAdCallback?.adLoaded(nativeAd)

            Logger.logDebug(TAG, "loadNativeAd:adAlreadyPreCached")

        } else {

            if (loadingNativeAd) {
                Logger.logDebug(TAG, "loadNativeAd:Already loading ad")
                return
            }

            loadingNativeAd = true

            val adLoader = AdLoader.Builder(context, adId).forNativeAd { newNativeAd ->

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