package com.sarmad.admobify.adsdk.utils.logger

import android.util.Log

enum class Level(val priority: Int) {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR)
}