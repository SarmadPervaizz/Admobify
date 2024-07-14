package com.sarmad.admobify.adsdk.utils

import android.util.Log

internal object Logger {

    fun logInfo(tag: String = "UnSpecified", msg: String) {
        Log.i(tag, msg)
    }

    fun logWarning(tag: String = "UnSpecified", msg: String) {
        Log.w(tag, msg)
    }

    fun logDebug(tag: String = "UnSpecified", msg: String) {
        Log.d(tag, msg)
    }

    fun logError(tag: String = "UnSpecified", msg: String) {
        Log.e(tag, msg)
    }
}