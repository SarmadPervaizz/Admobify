package com.sarmad.admobify.adsdk.app_open_ad

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
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
import com.sarmad.admobify.adsdk.utils.isShowingInterAd
import com.sarmad.admobify.adsdk.utils.isShowingOpenAd
import com.sarmad.admobify.adsdk.utils.isShowingRewardAd
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.logger.Logger
import com.sarmad.admobify.adsdk.utils.setShowingOpenAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class OpenAppAd : DefaultLifecycleObserver, ActivityLifecycleCallbacks {

    private var availableActivity: Activity? = null

    private var mContext: Context? = null

    private var openAdId: String? = null

    private val appLifeScope = ProcessLifecycleOwner.get().lifecycleScope

    private var shouldLoadAdAfterInit = false

    private var canReloadOnDismiss = false

    private var mLoadOnPause = false

    private var adRemote:Boolean = false

    companion object {

        private var appOpenAd: AppOpenAd? = null

        /** Keep track of the time an app open ad is
         *  loaded to ensure you don't show an expired ad. */

        private var openAdloadTime = 0L

        private var isLoadingOpenAd = false

        var unregisterLifecycle: (() -> Unit)? = null
    }

    fun init(
        application: Application,
        adId: String,
        remote:Boolean,
        preloadAd: Boolean,
        loadOnPause: Boolean,
        reloadOnDismiss: Boolean=false
    ) {
        this.shouldLoadAdAfterInit = preloadAd
        this.canReloadOnDismiss = reloadOnDismiss
        openAdId = adId
        adRemote = remote
        mContext = application
        mLoadOnPause = loadOnPause
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        application.registerActivityLifecycleCallbacks(this)

        unregisterLifecycle = {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
            application.unregisterActivityLifecycleCallbacks(this)
            appOpenAd = null
            isLoadingOpenAd = false
            openAdloadTime = 0L
        }
    }


    private fun loadAppOpenAd() {

        if (isLoadingOpenAd || isAdAvailable()) {

            when {

                isLoadingOpenAd -> {
                    Logger.log(Level.DEBUG, Category.OpenAd,
                        "already loading an ad"
                    )
                }

                isAdAvailable() -> {
                    Logger.log(
                        Level.DEBUG,
                        Category.OpenAd,
                        "ad is already loaded"
                    )
                }
            }

            return
        }

        val premiumUser = Admobify.isPremiumUser()
        val networkAvailable = AdmobifyUtils.isNetworkAvailable(mContext)

        if (adRemote && !premiumUser && networkAvailable){

            isLoadingOpenAd = true

            Logger.log(Level.DEBUG,Category.OpenAd,"requesting ad $openAdId")

            AppOpenAd.load(
                mContext ?: return, openAdId ?: return,
                AdRequest.Builder().build(), attachLoadCallback()
            )

        } else {

            Logger.log(Level.DEBUG,Category.OpenAd,"ad validate -> remote:$adRemote" +
                    " premiumUser:$premiumUser networkAvailable:$networkAvailable")
        }
    }


    private fun attachLoadCallback(): AppOpenAd.AppOpenAdLoadCallback {
        val callback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                appOpenAd = null
                isLoadingOpenAd = false

                Logger.log(
                    Level.ERROR,
                    Category.OpenAd,
                    "ad failed to load error code:${error.code} error msg:${error.message}"
                )

            }

            override fun onAdLoaded(ad: AppOpenAd) {
                isLoadingOpenAd = false
                openAdloadTime = Date().time
                appOpenAd = ad

                Logger.log(Level.DEBUG,Category.OpenAd, "ad loaded")

            }
        }
        return callback
    }


    private fun showAppOpenAd() {

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {

                appOpenAd = null

                setShowingOpenAd(false)

                Logger.log(Level.DEBUG,Category.OpenAd, "ad dismiss")

                if (canReloadOnDismiss){
                    Logger.log(Level.DEBUG,Category.OpenAd, "Reloading Open Ad")
                    loadAppOpenAd()
                }
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {

                Logger.log(Level.ERROR,Category.OpenAd, "failed to show error code:${error.code} error msg:${error.message}")

                setShowingOpenAd(false)

            }

            override fun onAdShowedFullScreenContent() {

                Logger.log(Level.DEBUG,Category.OpenAd, "ad show full screen")

                setShowingOpenAd(true)

            }


        }
        appLifeScope.launch {
            delay(200)
            appOpenAd?.show(availableActivity ?: return@launch)
        }
    }


    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && AdmobifyUtils.wasLoadTimeLessThanNHoursAgo(4, openAdloadTime)
    }


    private fun canShowOpenAd(): Boolean {
        return OpenAppAdState.isOpenAppAdEnabled() && !isShowingOpenAd() &&
                !isShowingInterAd() && !isShowingRewardAd()
    }


    override fun onStart(owner: LifecycleOwner) {
        if (isAdAvailable() && canShowOpenAd()) {
            showAppOpenAd()
        } else if (shouldLoadAdAfterInit) {
            loadAppOpenAd()
        }
    }

    /** Load Open Ad when user stops or minimizes the app
     * and when he will came back ad will be shown */

    override fun onStop(owner: LifecycleOwner) {
        Logger.log(Level.DEBUG,Category.OpenAd, "onStop")
        if (!shouldLoadAdAfterInit){
            loadAppOpenAd()
            shouldLoadAdAfterInit = true
        } else if (mLoadOnPause){
            loadAppOpenAd()
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