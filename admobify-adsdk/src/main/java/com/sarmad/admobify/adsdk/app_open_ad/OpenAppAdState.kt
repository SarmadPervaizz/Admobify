package com.sarmad.admobify.adsdk.app_open_ad

object OpenAppAdState {

    private var openAppEnabled = true

    private var screenName = "UNDEFINED"

    fun enableOpenAppAd(screenName:String="UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = true
    }

    fun disableOpenAppAd(screenName:String="UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = false
    }

    fun isOpenAppAdEnabled():Boolean = openAppEnabled

}