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
import java.lang.ref.WeakReference

internal object BannerAdLoader {

    private const val TAG = "BannerAdLoader"

    private var isLoadingBannerAd = false

    private var isLoadingCollapsibleBannerAd = false

    private var isLoadingRectangleBannerAd = false

    private var isLoadingInlineAdaptiveBannerAd = false

    private var defaultBannerCallback: BannerCallback? = null
    private var collapsibleBannerCallback: BannerCallback? = null
    private var rectangleBannerCallback: BannerCallback? = null
    private var inlineAdaptiveBannerCallback: BannerCallback? = null

    private var defaultBannerContainer: WeakReference<ViewGroup?>? = null
    private var collapsibleBannerContainer: WeakReference<ViewGroup?>? = null
    private var rectangleBannerContainer: WeakReference<ViewGroup?>? = null
    private var inlineAdaptiveBannerContainer: WeakReference<ViewGroup?>? = null


    fun loadBannerAd(
        context: Activity,
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        loadingOrShimmer: View?,
        callback: BannerCallback
    ) {

        defaultBannerCallback = callback

        defaultBannerContainer = WeakReference(container)

        if (isLoadingBannerAd) {
            Logger.logError(TAG, "Already loading an banner ad")
            return
        }

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            defaultBannerContainer?.get()?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    defaultBannerContainer?.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(
                        this
                    )

                    val width = defaultBannerContainer?.get()?.width ?: 0

                    val height = defaultBannerContainer?.get()?.height ?: 0

                    if (width < 320 || height < 50) {
                        Logger.logError(
                            TAG,
                            "width: ${width} or height: ${height} is invalid for default banner ad"
                        )
                        defaultBannerCallback?.onAdFailed(null)
                        return
                    }

                    isLoadingBannerAd = true

                    val adView = AdView(context)

                    val adSize = AdmobifyUtils.getAdSize(context, defaultBannerContainer?.get())

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
                        container = defaultBannerContainer?.get(),
                        loadingOrShimmer = loadingOrShimmer,
                        callback = defaultBannerCallback,
                        bannerAdType = BannerAdType.DEFAULT_BANNER
                    )

                    adView.loadAd(adRequest)
                }
            })

        } else {
            defaultBannerCallback?.onAdValidate()
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

        collapsibleBannerCallback = callback

        collapsibleBannerContainer = WeakReference(container)

        if (isLoadingCollapsibleBannerAd) {
            Logger.logError(TAG, "Already loading an collapsible banner ad")
            return
        }

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            collapsibleBannerContainer?.get()?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    collapsibleBannerContainer?.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(
                        this
                    )

                    val width = collapsibleBannerContainer?.get()?.width ?: 0

                    val height = collapsibleBannerContainer?.get()?.height ?: 0

                    if (width < 320 || height < 50) {
                        Logger.logError(
                            TAG,
                            "width: ${width} or height: ${height} is invalid for collapsible banner ad"
                        )
                        collapsibleBannerCallback?.onAdFailed(null)
                        return
                    }

                    isLoadingCollapsibleBannerAd = true

                    val adView = AdView(context)

                    val adSize = AdmobifyUtils.getAdSize(context, collapsibleBannerContainer?.get())

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
                        container = collapsibleBannerContainer?.get(),
                        loadingOrShimmer = loadingOrShimmer,
                        callback = collapsibleBannerCallback,
                        bannerAdType = BannerAdType.COLLAPSIBLE_BANNER
                    )

                    adView.loadAd(adRequest)
                }
            })
        } else {
            collapsibleBannerCallback?.onAdValidate()
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

        rectangleBannerCallback = callback

        rectangleBannerContainer = WeakReference(container)

        if (isLoadingRectangleBannerAd) {
            Logger.logError(TAG, "Already loading an rectangle banner ad")
            return
        }

        if (AdmobifyUtils.isNetworkAvailable(context) && remote && !Admobify.isPremiumUser()) {

            rectangleBannerContainer?.get()?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    rectangleBannerContainer?.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(
                        this
                    )

                    val width = rectangleBannerContainer?.get()?.width ?: 0

                    val height = rectangleBannerContainer?.get()?.height ?: 0

                    if (width < 300 || height < 250) {
                        Logger.logError(
                            TAG,
                            "width: $width or height: $height is invalid for rectangle banner ad"
                        )
                        rectangleBannerCallback?.onAdFailed(null)
                        return
                    }

                    isLoadingRectangleBannerAd = true

                    val adView = AdView(context)

                    adView.setAdSize(AdSize.MEDIUM_RECTANGLE)

                    adView.adUnitId = adId

                    val adRequest = AdRequest.Builder().build()

                    attachAdLoadCallback(
                        adView,
                        rectangleBannerContainer?.get(), loadingOrShimmer,
                        rectangleBannerCallback,
                        BannerAdType.RECTANGLE_BANNER
                    )

                    adView.loadAd(adRequest)

                }

            })
        } else {
            rectangleBannerCallback?.onAdValidate()
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

        inlineAdaptiveBannerCallback = callback

        inlineAdaptiveBannerContainer = WeakReference(container)

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
                inlineAdaptiveBannerContainer?.get(), loadingOrShimmer,
                inlineAdaptiveBannerCallback,
                BannerAdType.INLINE_ADAPTIVE_BANNER
            )

            adView.loadAd(adRequest)

        } else {
            inlineAdaptiveBannerCallback?.onAdValidate()
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

                when (bannerAdType) {
                    BannerAdType.DEFAULT_BANNER -> isLoadingBannerAd = false
                    BannerAdType.COLLAPSIBLE_BANNER -> isLoadingCollapsibleBannerAd = false
                    BannerAdType.RECTANGLE_BANNER -> isLoadingRectangleBannerAd = false
                    BannerAdType.INLINE_ADAPTIVE_BANNER -> isLoadingInlineAdaptiveBannerAd = false
                }

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

                when (bannerAdType) {
                    BannerAdType.DEFAULT_BANNER -> isLoadingBannerAd = false
                    BannerAdType.COLLAPSIBLE_BANNER -> isLoadingCollapsibleBannerAd = false
                    BannerAdType.RECTANGLE_BANNER -> isLoadingRectangleBannerAd = false
                    BannerAdType.INLINE_ADAPTIVE_BANNER -> isLoadingInlineAdaptiveBannerAd = false
                }

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



}