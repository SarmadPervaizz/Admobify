package com.sarmad.admobify.adsdk.app_open_ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.Logger
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.setShowingOpenAd
import java.util.Date

object SplashOpenAppAd {

    private const val LOG_TAG = "SplashOpenAppAd"

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

            Log.d(LOG_TAG, "isSplashOpenAdLoading: $isSplashOpenAdLoading")

            Log.d(LOG_TAG, "is Ad Available: ${isAdAvailable()}")

            return
        }

        val premiumUser = Admobify.isPremiumUser()
        val networkAvailable = AdmobifyUtils.isNetworkAvailable(context)

        if (remote && !premiumUser && networkAvailable) {

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

                Logger.logDebug(LOG_TAG, "onAdFailedToLoad")

                adFailed?.invoke(error)
            }

            override fun onAdLoaded(ad: AppOpenAd) {

                isSplashOpenAdLoading = false
                splashOpenAd = ad
                splashOpenAdLoadTime = Date().time

                Logger.logDebug(LOG_TAG, "onAdLoaded")

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
            Logger.logDebug(LOG_TAG, "showAppOpenAd -> called")

            splashOpenAd?.fullScreenContentCallback =
                attachAdShowCallback(adShowFullScreen, adFailedToShow, adDismiss)

            splashOpenAd?.show(activity)
        } else {

            Logger.logDebug(LOG_TAG, "isAdAvailable:${isAdAvailable()}")

            Logger.logDebug(LOG_TAG, "any other ad is showing:${!canShowOpenAd()}")

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

                Logger.logDebug(LOG_TAG, "onAdDismissedFullScreenContent")

                adDismiss.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {

                Logger.logDebug(LOG_TAG, "onAdFailedToShowFullScreenContent:${error.message}")

                setShowingOpenAd(false)

                adFailedToShow.invoke(error)

            }

            override fun onAdShowedFullScreenContent() {

                Logger.logDebug(LOG_TAG, "onAdShowedFullScreenContent")

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