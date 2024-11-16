package com.sarmad.admobify.adsdk.interstitial_ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils
import com.sarmad.admobify.adsdk.utils.LoadingDialog
import com.sarmad.admobify.adsdk.utils.logger.Logger
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.setShowingInterAd

class InterstitialAdUtils(builder: InterAdBuilder) {

    companion object {

        /** Interstitial Ad object which will be assigned with new instance
         * when ad is loaded and assigned null when ad is shown */
        internal var mInterstitialAd: InterstitialAd? = null

        fun isAdAvailable(): Boolean = mInterstitialAd != null

        /** To maintain state of loading ad to prevent sending multiple
         * load requests against single ad instance */
        internal var loadingInterstitialAd = false

        /** As fake delay function is also available for simply showing
         * ad show we have to check whether show ad is called from Load and
         * show or not */
        internal var fromLoadAndShow: Boolean = false

    }

    private var loadingDialog: LoadingDialog? = null

    var adOptions = builder.adOptions

    private var activity = adOptions.getActivity()

    /** To keep state of activity whether its paused after load and show request sent
     * so when user resume activity we don't sudden popup with ad */
    private var appPausedOnce = false


    /** Tells current state of app whether its in pause or resume state :-) */
    private var isPaused = false


    init {
        registerActivityLifeCycle()
    }

    private fun getNewAdRequest() = AdRequest.Builder().build()


    fun loadInterstitialAd(loadCallback: InterAdLoadCallback?) {


        AdmobifyUtils.validateAdMobAdUnitId(adOptions.getAdId())

        /**
         * Ad is already available no need to send
         * a new load request
         */

        if (mInterstitialAd != null) {
            Logger.log(Level.DEBUG,Category.Interstitial, "adAlreadyLoaded")
            loadCallback?.adAlreadyLoaded()
            return
        }

        /**
         * An Ad request is already being processed
         */

        if (loadingInterstitialAd) {
            val msg = "Already processing ad request"
            Logger.log(Level.DEBUG,Category.Interstitial, msg)
            loadCallback?.adFailed(null, msg)
            return
        }

        if (activity == null) {
            val msg = "Provided activity instance is null"
            Logger.log(Level.ERROR,Category.Interstitial, msg)
            loadCallback?.adFailed(null, msg)
            return
        }

        if (adOptions.isRemoteEnabled() &&
            !Admobify.isPremiumUser() &&
            AdmobifyUtils.isNetworkAvailable(activity)
        ) {

            Logger.log(Level.DEBUG, Category.Interstitial, "requesting ad ${adOptions.getAdId()}")

            loadingInterstitialAd = true

            InterstitialAd.load(activity?.applicationContext ?: return,
                adOptions.getAdId(),
                getNewAdRequest(),
                object : InterstitialAdLoadCallback() {

                    override fun onAdFailedToLoad(error: LoadAdError) {

                        Logger.log(Level.ERROR,Category.Interstitial, "onAdFailedToLoad code:${error.code} msg:${error.message}")

                        loadingInterstitialAd = false
                        loadCallback?.adFailed(error, null)
                    }

                    override fun onAdLoaded(interAd: InterstitialAd) {

                        Logger.log(Level.DEBUG,Category.Interstitial, "onAdLoaded")

                        loadingInterstitialAd = false
                        mInterstitialAd = interAd
                        loadCallback?.adLoaded()
                    }

                })

        } else {
            Logger.log(Level.DEBUG,Category.Interstitial, "adValidate")
            loadCallback?.adValidate()
        }
    }


    fun showInterstitialAd(adShowCallback: InterAdShowCallback?) {
        if (mInterstitialAd != null && adOptions.isRemoteEnabled() && !Admobify.isPremiumUser()) {

            if (isShowingInterAd() || isShowingRewardAd() || isShowingOpenAd()) {
                Logger.log(Level.DEBUG,Category.Interstitial, "Can't show ad An ad is already showing")
                return
            }

            if (!fromLoadAndShow) {

                if (adOptions.getDialogFakeDelay > 0) {
                    showLoadingDialog()
                }

                Handler(Looper.getMainLooper()).postDelayed({

                    if (!isPaused && !appPausedOnce) {
                        mInterstitialAd?.fullScreenContentCallback =
                            attachAdShowCallback(adShowCallback)
                        mInterstitialAd?.show(activity ?: return@postDelayed)
                        setShowingInterAd(true)
                    }

                }, adOptions.getDialogFakeDelay)

            }
            /** Called from load and show utils class */
            else {
                fromLoadAndShow = false
                mInterstitialAd?.fullScreenContentCallback = attachAdShowCallback(adShowCallback)
                mInterstitialAd?.show(activity ?: return)
                setShowingInterAd(true)
            }

            fromLoadAndShow = false
        } else {
            Logger.log(Level.DEBUG,Category.Interstitial, "adNotAvailable")
            adShowCallback?.adNotAvailable()
        }
    }


    fun loadAndShowInterAd(
        interAdLoadCallback: InterAdLoadCallback?, interAdShowCallback: InterAdShowCallback?,
        readyToNavigate: (() -> Unit)? = null
    ) {
        InterAdLoadAndShow(activity ?: return, this).loadAndShowInter(
            interAdLoadCallback,
            interAdShowCallback,
            readyToNavigate
        )
    }


    private fun attachAdShowCallback(adShowCallback: InterAdShowCallback?): FullScreenContentCallback {
        val interShowCallback = object : FullScreenContentCallback() {

            override fun onAdClicked() {
                Logger.log(Level.DEBUG,Category.Interstitial, "onAdClicked")

                adShowCallback?.adClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                Logger.log(Level.DEBUG,Category.Interstitial, "onAdDismissedFullScreenContent")

                mInterstitialAd = null
                setShowingInterAd(false)
                adShowCallback?.adDismiss()
                if (adOptions.canReloadOnDismiss()) {
                    loadInterstitialAd(null)
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Logger.log(Level.ERROR,Category.Interstitial, "ad failed to show " +
                        "error:${error.code} msg:${error.message}")

                mInterstitialAd = null
                setShowingInterAd(false)
                adShowCallback?.adFailedToShow()
            }

            override fun onAdImpression() {
                Logger.log(Level.DEBUG,Category.Interstitial, "onAdImpression")

                adShowCallback?.adImpression()
            }

            override fun onAdShowedFullScreenContent() {
                Logger.log(Level.DEBUG,Category.Interstitial, "onAdShowedFullScreenContent")

                setShowingInterAd(true)
                adShowCallback?.adShowFullScreen()
            }

        }
        return interShowCallback
    }

    private fun registerActivityLifeCycle() {
        activity?.application?.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(currActivity: Activity, p1: Bundle?) {

            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityResumed(currActivity: Activity) {
                if (currActivity == activity) {
                    isPaused = false
                }
            }

            override fun onActivityPaused(currActivity: Activity) {
                if (currActivity == activity) {
                    appPausedOnce = true
                    isPaused = true
                    dismissLoadingDialog()
                }
            }

            override fun onActivityStopped(currActivity: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(currActivity: Activity) {

            }
        })
    }


    private fun showLoadingDialog() {

        if (activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }

        loadingDialog =
            LoadingDialog(activity ?: return, adOptions.getCustomLoadingLayout(), adOptions.isFullScreenLoading())
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }


}