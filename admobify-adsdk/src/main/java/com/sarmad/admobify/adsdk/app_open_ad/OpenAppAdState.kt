package com.sarmad.admobify.adsdk.app_open_ad

object OpenAppAdState {

    private var openAppEnabled = true

    private var screenName = "UNDEFINED"

    fun enable(screenName: String = "UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = true
    }

    fun disable(screenName: String = "UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = false
    }

    fun isOpenAppAdEnabled(): Boolean = openAppEnabled

}