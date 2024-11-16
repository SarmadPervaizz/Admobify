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
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.logger.Logger

internal object IntroNativeAdLoader {

    private var nativeAdCallback: NativeAdCallback? = null

    private var nativeAd: NativeAd? = null

    private var loadingNativeAd = false


    fun loadNativeAd(
        application: Application,
        adId: String,
        remote: Boolean,
        adListener: NativeAdCallback
    ) {

        val network = AdmobifyUtils.isNetworkAvailable(application)

        if (remote && network && !Admobify.isPremiumUser()) {

            nativeAdCallback = adListener

            if (nativeAd != null) {

                nativeAdCallback?.adLoaded(nativeAd)

                Logger.log(Level.DEBUG,Category.Native, "intro ad already pre cached")

            } else {

                if (loadingNativeAd) {
                    Logger.log(Level.DEBUG,Category.Native,  "intro ad already loading")
                    return
                }

                loadingNativeAd = true

                val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
                val nativeOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

                val adLoader =
                    AdLoader.Builder(application, adId).withNativeAdOptions(nativeOptions)
                        .forNativeAd { newNativeAd ->

                            nativeAd = newNativeAd

                        }.withAdListener(attachAdListener()).build()

                adLoader.loadAd(AdRequest.Builder().build())

            }

        } else {

            Logger.log(Level.DEBUG,Category.Native,
                "adValidate: remote:$remote network:$network" +
                        " premium user:${Admobify.isPremiumUser()}"
            )

            adListener.adValidate()
        }
    }


    private fun attachAdListener(): AdListener {
        val listener = object : AdListener() {

            override fun onAdClicked() {
                nativeAdCallback?.adClicked()
                Logger.log(Level.DEBUG,Category.Native, "intro ad clicked")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loadingNativeAd = false
                nativeAdCallback?.adFailed(error)
                Logger.log(Level.ERROR, Category.Native, "intro ad failed to load " +
                        "code:${error.code} msg:${error.message}")
            }

            override fun onAdImpression() {
                nativeAd = null
                nativeAdCallback?.adImpression()

                Logger.log(Level.DEBUG, Category.Native, "intro ad impression")

            }

            override fun onAdLoaded() {
                loadingNativeAd = false
                nativeAdCallback?.adLoaded(nativeAd)

                Logger.log(Level.DEBUG, Category.Native, "intro ad loaded")

            }

        }

        return listener
    }


}