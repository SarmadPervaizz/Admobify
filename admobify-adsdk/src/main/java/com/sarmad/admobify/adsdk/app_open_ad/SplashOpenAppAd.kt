package com.sarmad.admobify.adsdk.app_open_ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.logger.Logger
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.setShowingOpenAd
import java.util.Date

object SplashOpenAppAd {

    private var splashOpenAd: AppOpenAd? = null

    private var isSplashOpenAdLoading = false

    private var splashOpenAdLoadTime = 0L


    fun loadOpenAppAd(
        context: Context,
        adId: String,
        remote: Boolean,
        adLoaded: (() -> Unit)?,
        adFailed: ((error: LoadAdError) -> Unit)?,
        adValidate: (() -> Unit)?
    ) {

        if (isSplashOpenAdLoading || isAdAvailable()) {

            when {

                isSplashOpenAdLoading -> {
                    Logger.log(Level.DEBUG, Category.SplashOpenAd,
                        "already loading an ad"
                    )
                }

                isAdAvailable() -> {
                    Logger.log(
                        Level.DEBUG,
                        Category.SplashOpenAd,
                        "ad is already loaded"
                    )
                }
            }

            return
        }

        val premiumUser = Admobify.isPremiumUser()
        val networkAvailable = AdmobifyUtils.isNetworkAvailable(context)

        if (remote && !premiumUser && networkAvailable) {

            Logger.log(Level.DEBUG, Category.SplashOpenAd, "requesting ad $adId")

            isSplashOpenAdLoading = true

            AppOpenAd.load(
                context, adId,
                getAdRequest(), attachLoadCallback(adLoaded, adFailed)
            )
        } else {
            adValidate?.invoke()
        }
    }

    private fun attachLoadCallback(
        adLoaded: (() -> Unit)?,
        adFailed: ((error: LoadAdError) -> Unit)?
    ): AppOpenAd.AppOpenAdLoadCallback {
        val callback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {

                splashOpenAd = null
                isSplashOpenAdLoading = false

                 Logger.log(Level.ERROR, Category.SplashOpenAd, "onAdFailedToLoad error code:${error.code} error msg:${error.message}")

                adFailed?.invoke(error)
            }

            override fun onAdLoaded(ad: AppOpenAd) {

                isSplashOpenAdLoading = false
                splashOpenAd = ad
                splashOpenAdLoadTime = Date().time

                 Logger.log(Level.DEBUG, Category.SplashOpenAd, "onAdLoaded")

                adLoaded?.invoke()

            }
        }
        return callback
    }

    fun showOpenAppAd(
        activity: Activity, adShowFullScreen: () -> Unit,
        adFailedToShow: (error: AdError?) -> Unit,
        adDismiss: () -> Unit
    ) {
        if (isAdAvailable() && canShowOpenAd()) {

            splashOpenAd?.fullScreenContentCallback =
                attachAdShowCallback(adShowFullScreen, adFailedToShow, adDismiss)

            splashOpenAd?.show(activity)
        } else {

            when {
                !isAdAvailable() -> {
                    Logger.log(Level.DEBUG, Category.SplashOpenAd, "ad not available")
                }

                !canShowOpenAd() -> {
                    Logger.log(Level.DEBUG, Category.SplashOpenAd, "any other ad is showing")
                }
            }

            adFailedToShow.invoke(null)
        }
    }


    private fun attachAdShowCallback(
        adShowFullScreen: () -> Unit,
        adFailedToShow: (error: AdError?) -> Unit,
        adDismiss: () -> Unit
    ): FullScreenContentCallback {
        val adShow = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {

                splashOpenAd = null

                setShowingOpenAd(false)

                 Logger.log(Level.DEBUG, Category.SplashOpenAd, "ad dismiss full screen")

                adDismiss.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Logger.log(
                    Level.ERROR,
                    Category.SplashOpenAd,
                    "ad failed to show error code:${error.code} error msg:${error.message}"
                )

                setShowingOpenAd(false)

                adFailedToShow.invoke(error)

            }

            override fun onAdShowedFullScreenContent() {

                 Logger.log(Level.DEBUG, Category.SplashOpenAd, "ad show full screen")

                setShowingOpenAd(true)

                adShowFullScreen.invoke()
            }
        }
        return adShow
    }


    private fun canShowOpenAd(): Boolean {
        return !isShowingOpenAd() && !isShowingInterAd() && !isShowingRewardAd()
    }

    private fun getAdRequest() = AdRequest.Builder().build()

    private fun isAdAvailable(): Boolean {
        return splashOpenAd != null &&
                AdmobifyUtils.wasLoadTimeLessThanNHoursAgo(4, splashOpenAdLoadTime)
    }

}