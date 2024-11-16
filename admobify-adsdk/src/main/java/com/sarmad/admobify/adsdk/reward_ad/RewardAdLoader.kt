package com.sarmad.admobify.adsdk.reward_ad

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.logger.Logger
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.setShowingRewardAd

internal object RewardAdLoader {

    fun loadRewardAd(
        context: Activity,
        remote: Boolean,
        adUnit: String,
        callback: RewardAdLoadCallback
    ) {

        val networkAvailable = AdmobifyUtils.isNetworkAvailable(context)

        if (networkAvailable && remote && !Admobify.isPremiumUser()) {


            if (RewardedAdUtils.getRewardAd() != null) {
                callback.adAlreadyLoaded()
                Logger.log(Level.DEBUG, Category.Rewarded, "ad already loaded")
                return
            }

            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(context, adUnit, adRequest, adLoadListener(callback) )

        } else {

            logValidateError(
                networkAvailable = networkAvailable,
                remote = remote
            )
        }

    }

    private fun adLoadListener(callback: RewardAdLoadCallback):RewardedAdLoadCallback{
        val listener = object : RewardedAdLoadCallback() {

            override fun onAdFailedToLoad(error: LoadAdError) {

                Logger.log(
                    Level.ERROR,
                    Category.Rewarded,
                    "ad failed to load error:${error.code} msg:${error.message}"
                )

                RewardedAdUtils.setRewardAd(null)
                callback.adFailed(error, null)
            }

            override fun onAdLoaded(reward: RewardedAd) {
                Logger.log(Level.DEBUG, Category.Rewarded, "onAdLoaded")

                RewardedAdUtils.setRewardAd(reward)

                callback.adLoaded()
            }

        }
        return listener
    }


    fun showRewardAd(context: Activity, remote: Boolean, callback: RewardAdShowCallback) {
        val networkAvailable = AdmobifyUtils.isNetworkAvailable(context)

        if (networkAvailable && remote && !Admobify.isPremiumUser()) {

            if (RewardedAdUtils.getRewardAd() == null) {
                callback.adNotAvailable()
                Logger.log(Level.DEBUG, Category.Rewarded,"adNotAvailable")
                return
            }

            if (isShowingRewardAd() || isShowingInterAd() || isShowingOpenAd()){
                Logger.log(Level.DEBUG, Category.Rewarded,"Can't show ad An ad is already showing")
                return
            }

            RewardedAdUtils.getRewardAd()?.fullScreenContentCallback = adShowListener(callback)

            RewardedAdUtils.getRewardAd()?.show(context) {
                callback.rewardEarned()
            }

            setShowingRewardAd(true)

        } else {

            callback.adValidate()

            logValidateError("show", networkAvailable, remote)
        }
    }

    private fun adShowListener(callback: RewardAdShowCallback): FullScreenContentCallback {

        val listener = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Logger.log(Level.DEBUG, Category.Rewarded,"onAdClicked")

                callback.adClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                Logger.log(Level.DEBUG, Category.Rewarded,"ad dismiss")

                callback.adDismiss()

                RewardedAdUtils.setRewardAd(null)
                setShowingRewardAd(false)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {

                Logger.log(
                    Level.ERROR,
                    Category.Rewarded,
                    "ad failed to show error:${error.code} msg:${error.message}"
                )

                callback.adFailedToShow(error)

                setShowingRewardAd(false)
            }

            override fun onAdImpression() {
                Logger.log(Level.DEBUG, Category.Rewarded,"ad impression")

                callback.adImpression()
            }

            override fun onAdShowedFullScreenContent() {
                Logger.log(Level.DEBUG, Category.Rewarded,"ad show full screen")

                callback.adShowFullScreen()
                setShowingRewardAd(true)
            }
        }

        return listener
    }


    private fun logValidateError(
        showOrLoad: String = "load",
        networkAvailable: Boolean,
        remote: Boolean
    ) {

        val msg = "can't $showOrLoad ad internet available:$networkAvailable " +
                "remote:$remote premium user:${Admobify.isPremiumUser()}"

        Logger.log(Level.ERROR, Category.Rewarded, msg)

    }


}