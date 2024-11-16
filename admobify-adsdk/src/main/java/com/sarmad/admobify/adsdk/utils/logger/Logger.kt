package com.sarmad.admobify.adsdk.utils.logger

import android.util.Log
import com.sarmad.admobify.adsdk.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object Logger {

    private const val LOG_TAG = "Ads_"

    private var loggingEnabled = false

    fun enableLogging(){
        loggingEnabled = true
    }

    internal fun log(
        level: Level, category: Category,
        msg: String, exception: Throwable? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            if (!BuildConfig.DEBUG && !loggingEnabled) return@launch

            val tag = LOG_TAG + category.name
            when (level) {
                Level.VERBOSE -> {
                    Log.v(tag, msg, exception)
                }

                Level.DEBUG -> {
                    Log.d(tag, msg, exception)
                }

                Level.INFO -> {
                    Log.i(tag, msg, exception)
                }

                Level.WARN -> {
                    Log.w(tag, msg, exception)
                }

                Level.ERROR -> {
                    Log.e(tag, msg, exception)
                }
            }

        }
    }

}