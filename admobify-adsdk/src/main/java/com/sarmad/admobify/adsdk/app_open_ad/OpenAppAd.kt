package com.sarmad.admobify.adsdk.app_open_ad

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

private const val LOG_TAG = "OpenAppAd"

class OpenAppAd : DefaultLifecycleObserver, ActivityLifecycleCallbacks {

    private var availableActivity: Activity? = null

    private var mContext: Context? = null

    var appOpenAd: AppOpenAd? = null

    private var openAdId: String? = null

    private var adRemote:Boolean = false

    var isAdLoadRequested = false

    private val appLifeScope = ProcessLifecycleOwner.get().lifecycleScope

    /** Keep track of the time an app open ad is
     *  loaded to ensure you don't show an expired ad. */
    var loadTime = 0L

    private var shouldLoadAdAfterInit = false

    private var canReloadOnDismiss = false

    fun init(
        activity: Activity,
        adId: String,
        remote:Boolean,
        preloadAd: Boolean,
        reloadOnDismiss: Boolean
    ) {
        this.shouldLoadAdAfterInit = preloadAd
        this.canReloadOnDismiss = reloadOnDismiss
        openAdId = adId
        adRemote = remote
        mContext = activity.applicationContext
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        activity.application.registerActivityLifecycleCallbacks(this)
    }


    private fun loadAppOpenAd() {

        if (isAdLoadRequested || isAdAvailable()) {

            Log.e(LOG_TAG, "is Ad Load Requested: $isAdLoadRequested")

            Log.e(LOG_TAG, "is Ad Available: ${isAdAvailable()}")

            return
        }

        val premiumUser = Admobify.isPremiumUser()
        val networkAvailable = AdmobifyUtils.isNetworkAvailable(mContext)

        if (adRemote && !premiumUser && networkAvailable){

            isAdLoadRequested = true

            Logger.logDebug(LOG_TAG,"loading ad")

            AppOpenAd.load(
                mContext ?: return, openAdId ?: return,
                AdRequest.Builder().build(), attachLoadCallback()
            )

        } else {
            Logger.logError(LOG_TAG,"ad validate -> remote:$adRemote" +
                    " premiumUser:$premiumUser networkAvailable:$networkAvailable")
        }
    }


    private fun attachLoadCallback(): AppOpenAd.AppOpenAdLoadCallback {
        val callback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                appOpenAd = null
                isAdLoadRequested = false

                Logger.logDebug(LOG_TAG, "onAdFailedToLoad")
            }

            override fun onAdLoaded(ad: AppOpenAd) {
                isAdLoadRequested = false
                loadTime = Date().time
                appOpenAd = ad

                Logger.logDebug(LOG_TAG, "onAdLoaded")

            }
        }
        return callback
    }


    private fun showAppOpenAd() {

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {

                appOpenAd = null

                setShowingOpenAd(false)

                Logger.logDebug(LOG_TAG, "onAdDismissedFullScreenContent")

                if (canReloadOnDismiss){
                    Logger.logDebug(LOG_TAG, "Reloading Open Ad")
                    loadAppOpenAd()
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {

                Logger.logDebug(LOG_TAG, "onAdFailedToShowFullScreenContent:${error.message}")

                setShowingOpenAd(false)

            }

            override fun onAdShowedFullScreenContent() {

                Logger.logDebug(LOG_TAG, "onAdShowedFullScreenContent")

                setShowingOpenAd(true)

            }
        }
        appLifeScope.launch {
            delay(200)
            appOpenAd?.show(availableActivity ?: return@launch)
        }
    }


    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && AdmobifyUtils.wasLoadTimeLessThanNHoursAgo(4, loadTime)
    }


    private fun canShowOpenAd(): Boolean {
        return OpenAppAdState.isOpenAppAdEnabled() && !isShowingOpenAd() &&
                !isShowingInterAd() && !isShowingRewardAd()
    }


    override fun onStart(owner: LifecycleOwner) {
        if (isAdAvailable() && canShowOpenAd()) {
            Logger.logDebug(LOG_TAG, "showAppOpenAd -> called")
            showAppOpenAd()
        } else if (shouldLoadAdAfterInit) {
            Logger.logDebug(LOG_TAG, "loadAppOpenAd -> called")
            loadAppOpenAd()
        }
    }

    /** Load Open Ad when user stops or minimizes the app
     * and when he will came back ad will be shown */

    override fun onStop(owner: LifecycleOwner) {
        Logger.logDebug(LOG_TAG, "onStop")
        if (!shouldLoadAdAfterInit){
            loadAppOpenAd()
            shouldLoadAdAfterInit = true
        }
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        availableActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        availableActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        availableActivity = null
    }

}