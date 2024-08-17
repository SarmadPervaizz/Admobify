package com.sarmad.admobify.adsdk.banner_ads

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.Logger

internal object BannerAdLoaderNew {

    private const val TAG = "BannerAdLoaderNew"

    private var isLoadingBannerAd = false

    private var isLoadingCollapsibleBannerAd = false

    private var isLoadingRectangleBannerAd = false

    private var isLoadingInlineAdaptiveBannerAd = false


    fun loadBannerAd(
        context: Activity,
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        loadingOrShimmer: View?,
        callback: BannerCallback
    ) {

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            if (isLoadingBannerAd) {
                Logger.logError(TAG, "Already loading an banner ad")
                return
            }

            container.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    container.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val width = container.width

                    val height = container.height

                    if (width < 320 || height < 50) {
                        Logger.logError(
                            TAG,
                            "width: ${width} or height: ${height} is invalid for default banner ad"
                        )
                        callback.onAdFailed(null)
                        return
                    }

                    isLoadingBannerAd = true

                    val adView = AdView(context)

                    val adSize = AdmobifyUtils.getAdSize(context, container)

                    if (adSize == null) {
                        Logger.logError(TAG, "banner ad size should not be null")
                        callback.onAdFailed(null)
                        return
                    }

                    adView.setAdSize(adSize)

                    adView.adUnitId = adId

                    val adRequest = AdRequest.Builder().build()

                    attachAdLoadCallback(
                        adView = adView,
                        container = container,
                        loadingOrShimmer = loadingOrShimmer,
                        callback = callback,
                        bannerAdType = BannerAdType.DEFAULT_BANNER
                    )

                    adView.loadAd(adRequest)
                }
            })

        } else {
            callback.onAdValidate()
        }

    }

    fun loadCollapsibleBannerAd(
        context: Activity,
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        loadingOrShimmer: View?,
        callback: BannerCallback
    ) {

        if (isLoadingCollapsibleBannerAd) {
            Logger.logError(TAG, "Already loading an collapsible banner ad")
            return
        }

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            container.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    container.viewTreeObserver?.removeOnGlobalLayoutListener(
                        this
                    )

                    val width = container.width ?: 0

                    val height = container.height ?: 0

                    if (width < 320 || height < 50) {
                        Logger.logError(
                            TAG,
                            "width: ${width} or height: ${height} is invalid for collapsible banner ad"
                        )
                        callback.onAdFailed(null)
                        return
                    }

                    isLoadingCollapsibleBannerAd = true

                    val adView = AdView(context)

                    val adSize = AdmobifyUtils.getAdSize(context, container)

                    if (adSize == null) {
                        Logger.logError(TAG, "banner ad size should not be null")
                        callback.onAdFailed(null)
                        return
                    }

                    adView.setAdSize(adSize)
                    adView.adUnitId = adId

                    val extras = Bundle()
                    extras.putString("collapsible", "bottom")

                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()

                    attachAdLoadCallback(
                        adView = adView,
                        container = container,
                        loadingOrShimmer = loadingOrShimmer,
                        callback = callback,
                        bannerAdType = BannerAdType.COLLAPSIBLE_BANNER
                    )

                    adView.loadAd(adRequest)
                }
            })
        } else {
            callback.onAdValidate()
        }

    }

    fun loadRectangleBannerAd(
        context: Activity,
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        loadingOrShimmer: View?,
        callback: BannerCallback
    ) {

        //300 x 250

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            if (isLoadingRectangleBannerAd) {
                Logger.logError(TAG, "Already loading an rectangle banner ad")
                return
            }

            container.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    container.viewTreeObserver?.removeOnGlobalLayoutListener(
                        this
                    )

                    val width = container.width ?: 0

                    val height = container.height ?: 0

                    if (width < 300 || height < 250) {
                        Logger.logError(
                            TAG,
                            "width: $width or height: $height is invalid for rectangle banner ad"
                        )
                        callback?.onAdFailed(null)
                        return
                    }

                    isLoadingRectangleBannerAd = true

                    val adView = AdView(context)

                    adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

                    adView.adUnitId = adId

                    val adRequest = AdRequest.Builder().build()

                    attachAdLoadCallback(
                        adView,
                        container, loadingOrShimmer,
                        callback,
                        BannerAdType.RECTANGLE_BANNER
                    )

                    adView.loadAd(adRequest)

                }

            })
        } else {
            callback.onAdValidate()
        }
    }


    fun loadInlineAdaptiveBanner(
        context: Activity,
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        loadingOrShimmer: View?,
        adaptiveBannerHeight: Int,
        callback: BannerCallback
    ) {

        if (isLoadingInlineAdaptiveBannerAd) {
            Logger.logError(TAG, "Already loading an inline adaptive banner ad")
            return
        }

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            isLoadingInlineAdaptiveBannerAd = true

            val adView = AdView(context)

            val adSize = AdmobifyUtils.getAdaptiveAdSize(context, container, adaptiveBannerHeight)

            if (adSize == null) {
                Logger.logError(TAG, "banner ad size should not be null")
                callback.onAdFailed(null)
                return
            }

            adView.setAdSize(adSize)

            adView.adUnitId = adId

            val adRequest = AdRequest.Builder().build()

            attachAdLoadCallback(
                adView,
                container, loadingOrShimmer,
                callback,
                BannerAdType.INLINE_ADAPTIVE_BANNER
            )

            adView.loadAd(adRequest)

        } else {
            callback.onAdValidate()
        }
    }


    private fun attachAdLoadCallback(
        adView: AdView,
        container: ViewGroup?,
        loadingOrShimmer: View?,
        callback: BannerCallback?,
        bannerAdType: BannerAdType
    ) {
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {

                Logger.logError(TAG, "onAdFailedToLoad:${error.message} -> ${bannerAdType.name}")

                loadingOrShimmer?.visibility = View.GONE
                container?.visibility = View.GONE
                callback?.onAdFailed(error)

                updateLoadingStatus(bannerAdType)

            }

            override fun onAdImpression() {
                Logger.logDebug(TAG, "onAdImpression -> ${bannerAdType.name}")

                callback?.onAdImpression()
            }

            override fun onAdLoaded() {
                Logger.logDebug(TAG, "onAdLoaded -> ${bannerAdType.name}")

                loadingOrShimmer?.visibility = View.GONE
                container?.removeAllViews()
                container?.addView(adView)
                callback?.onAdLoaded(adView)


               updateLoadingStatus(bannerAdType)

            }

            override fun onAdClicked() {
                Logger.logDebug(TAG, "onAdClicked")
                callback?.onAdClick()
            }

            override fun onAdClosed() {
                Logger.logDebug(TAG, "onAdClosed")
                callback?.onAdClose()
            }

            override fun onAdOpened() {
                Logger.logDebug(TAG, "onAdOpened")
                callback?.onAdOpen()
            }

            override fun onAdSwipeGestureClicked() {
                Logger.logDebug(TAG, "onAdSwipeGestureClicked")
                callback?.onAdSwipeGestureClicked()
            }

        }
    }


    private fun updateLoadingStatus(bannerAdType: BannerAdType){
        when (bannerAdType) {
            BannerAdType.DEFAULT_BANNER_NEW_INSTANCE -> {
                isLoadingBannerAd = false
            }
            BannerAdType.COLLAPSIBLE_BANNER_NEW_INSTANCE -> {
                isLoadingCollapsibleBannerAd = false
            }
            BannerAdType.RECTANGLE_BANNER_NEW_INSTANCE -> {
                isLoadingRectangleBannerAd = false
            }
            BannerAdType.INLINE_ADAPTIVE_BANNER_NEW_INSTANCE -> {
                isLoadingInlineAdaptiveBannerAd = false
            }
            else -> {}
        }
    }

}