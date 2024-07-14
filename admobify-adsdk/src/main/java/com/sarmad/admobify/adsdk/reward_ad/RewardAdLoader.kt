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
import com.sarmad.admobify.adsdk.utils.Logger
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.setShowingRewardAd

internal object RewardAdLoader {
    private const val TAG = "RewardAdLoader"

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
                Logger.logDebug(TAG, "adAlreadyLoaded")

                return
            }

            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(context, adUnit, adRequest, adLoadListener(callback) )

        } else {

            logValidateError(
                networkAvailable = networkAvailable,
                remote = remote,
                context = context
            )
        }

    }

    private fun adLoadListener(callback: RewardAdLoadCallback):RewardedAdLoadCallback{
        val listener = object : RewardedAdLoadCallback() {

            override fun onAdFailedToLoad(error: LoadAdError) {

                Logger.logDebug(TAG, "onAdFailedToLoad:${error.message}")

                RewardedAdUtils.setRewardAd(null)
                callback.adFailed(error, null)
            }

            override fun onAdLoaded(reward: RewardedAd) {
                Logger.logDebug(TAG, "onAdLoaded")

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
                Logger.logDebug(TAG,"adNotAvailable")
                return
            }

            if (isShowingRewardAd() || isShowingInterAd() || isShowingOpenAd()){
                Logger.logDebug(TAG,"Can't show ad An ad is already showing")
                return
            }

            RewardedAdUtils.getRewardAd()?.fullScreenContentCallback = adShowListener(callback)

            RewardedAdUtils.getRewardAd()?.show(context) {
                callback.rewardEarned()
            }

            setShowingRewardAd(true)

        } else {

            callback.adValidate()

            logValidateError("show", networkAvailable, remote, context)
        }
    }

    private fun adShowListener(callback: RewardAdShowCallback): FullScreenContentCallback {

        val listener = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                callback.adClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                callback.adDismiss()

                RewardedAdUtils.setRewardAd(null)
                setShowingRewardAd(false)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                callback.adFailedToShow(error)
                setShowingRewardAd(false)
            }

            override fun onAdImpression() {
                callback.adImpression()
            }

            override fun onAdShowedFullScreenContent() {
                callback.adShowFullScreen()
                setShowingRewardAd(true)
            }
        }

        return listener
    }


    private fun logValidateError(
        showOrLoad: String = "load",
        networkAvailable: Boolean,
        remote: Boolean,
        context: Activity
    ) {

        val msg = "can't $showOrLoad ad internet available:$networkAvailable " +
                "remote:$remote premium user:${Admobify.isPremiumUser()}"

        Logger.logError(TAG, msg)

        if (AdmobifyUtils.isDebug()) {
            AdmobifyUtils.showSnackBar(context, msg)
        }
    }


}