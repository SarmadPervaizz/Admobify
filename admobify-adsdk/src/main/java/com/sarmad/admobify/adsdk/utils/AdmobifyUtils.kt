package com.sarmad.admobify.adsdk.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdSize
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.math.max

object AdmobifyUtils {

    private const val LOG_TAG = "AdmobifyUtils"

    private var appLifecycleScope = ProcessLifecycleOwner.get().lifecycleScope

    private val testIds = mutableListOf(
        "ca-app-pub-3940256099942544/9257395921",
        "ca-app-pub-3940256099942544/9214589741",
        "ca-app-pub-3940256099942544/6300978111",
        "ca-app-pub-3940256099942544/2014213617",
        "ca-app-pub-3940256099942544/1033173712",
        "ca-app-pub-3940256099942544/2247696110",
        "ca-app-pub-3940256099942544/5224354917"
    )

    fun showSnackBar(context: Activity, msg: String) {
        val rootView: View? = context.findViewById(android.R.id.content)
        Snackbar.make(rootView ?: return, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun isDebug(): Boolean {

        val buildFlavor = Admobify.mBuildFlavor

        val buildVariant = Admobify.mBuildVariant

        val devDebug = buildFlavor == "appDev" && buildVariant == "debug"

        val devRelease = buildFlavor == "appDev" && buildVariant == "release"

        val prodDebug = buildFlavor == "appProd" && buildVariant == "debug"

        return devDebug || devRelease || prodDebug
    }

    fun wasLoadTimeLessThanNHoursAgo(numHours: Long, loadTime: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }


    internal fun View.hide() {
        visibility = View.GONE
    }

    internal fun View.show() {
        visibility = View.VISIBLE
    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkAvailableAbove6(context)
        } else {
            networkAvailableBelow6(context)
        }
    }

    private fun networkAvailableBelow6(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun networkAvailableAbove6(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        val network = connectivityManager?.activeNetwork

        val capabilities = connectivityManager?.getNetworkCapabilities(network)

        if (capabilities != null) {

            val internetCapability =
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val capabilityValidated =
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            return internetCapability && capabilityValidated
        }

        return false
    }


    fun validateAdMobAdUnitId(adUnitId: String) {

        appLifecycleScope.launch(Dispatchers.IO) {

            //ca-app-pub-3940256099942544/1033173712

            val expectedLength = 38

            if (adUnitId.length < expectedLength || adUnitId.length > expectedLength) {
                val msg = "Provided Ad id length is not valid"
                throwExceptionOrLogError(msg)
                return@launch
            }


            if (!adUnitId.startsWith("ca-app-pub-")) {
                val msg = "Ad id should start with ca-app-pub-"
                throwExceptionOrLogError(msg)
                return@launch
            }

            val pubAndAdID = adUnitId.replace("ca-app-pub-", "")

            val split = pubAndAdID.split("/")

            if (split.size != 2) {
                throwExceptionOrLogError("Publisher OR Ad ID not found please recheck your Ad Id")
                return@launch
            }


            val publisherId = split[0]

            val adId = split[1]

            if (publisherId.length != 16) {
                val greaterOrLess = if (publisherId.length > 16) "greater" else "less"
                throwExceptionOrLogError("Invalid Publisher ID -> Publisher Ad ID $greaterOrLess then 16")
                return@launch
            }

            if (adId.length != 10) {
                val greaterOrLess = if (adId.length > 10) "greater" else "less"
                throwExceptionOrLogError("Invalid Ad ID -> Ad ID $greaterOrLess then 10")
                return@launch
            }

            if (!publisherId.all { it.isDigit() }) {
                throwExceptionOrLogError("Invalid characters found in publisher ID")
                return@launch
            }


            if (!adId.all { it.isDigit() }) {
                throwExceptionOrLogError("Invalid characters found in Ad ID")
                return@launch
            }

            if (isDebug() && !testIds.contains(adUnitId)) {
                throwExceptionOrLogError("Production ad ids can't be called in debug mode")
                return@launch
            }

            if (!isDebug() && testIds.contains(adUnitId)) {
                throwExceptionOrLogError("Debug ad ids can't be called in release mode")
                return@launch
            }

        }
    }


    private fun throwExceptionOrLogError(msg: String) {

        val adValidation = "AD_VALIDATION"

        if (Admobify.canThrowException()) {
            throw InvalidAdIDException(msg)
        } else {
            Logger.logError(adValidation, msg)
        }
    }

    //TODO: just use all ads for once then push to github

    fun getAdSize(activity: Activity, container: ViewGroup?): AdSize? {
        try {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = container?.width?.toFloat() ?: 0f

            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        } catch (e: Exception) {
            Logger.logError(LOG_TAG, "error getting banner adsize:${e.message}")
        }
        return null
    }


    fun getAdaptiveAdSize(activity: Activity, container: ViewGroup?, maxHeight: Int): AdSize? {
        try {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = container?.width?.toFloat() ?: 0f

            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getInlineAdaptiveBannerAdSize(adWidth, maxHeight)
        } catch (e: Exception) {
            Logger.logError(LOG_TAG, "error getting adaptive adsize:${e.message}")
        }
        return null
    }


}




