package com.sarmad.admobify.adsdk.utils.logger

import android.util.Log
import com.sarmad.admobify.adsdk.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Logger {

    private const val LOG_TAG = "Ads_"

    /** In Release variant logging is disabled by default */
    private var loggingEnabled = false

    /** Enable logging in Release variant */
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
                    exception?.let { Log.v(tag, msg, exception) } ?: Log.v(tag, msg)
                }

                Level.DEBUG -> {
                    exception?.let { Log.d(tag, msg, exception) } ?: Log.d(tag, msg)
                }

                Level.INFO -> {
                    exception?.let { Log.i(tag, msg, exception) } ?: Log.i(tag, msg)
                }

                Level.WARN -> {
                    exception?.let { Log.w(tag, msg, exception) } ?: Log.w(tag, msg)
                }

                Level.ERROR -> {
                    exception?.let { Log.e(tag, msg, exception) } ?: Log.e(tag, msg)
                }
            }

        }
    }

}