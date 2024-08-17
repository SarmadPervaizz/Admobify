package com.sarmad.admobify.adsdk.app_open_ad

object OpenAppAdState {

    private var openAppEnabled = true

    private var wasEnabledBefore = false

    private var screenName = "UNDEFINED"

    fun enable(screenName:String="UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = true
        if (wasEnabledBefore){
            OpenAppAd.adRemote = true
            wasEnabledBefore = false
        }
    }

    fun disable(screenName:String="UNDEFINED") {
        this.screenName = screenName
        openAppEnabled = false
        if (OpenAppAd.adRemote){
            wasEnabledBefore = true
            OpenAppAd.adRemote = false
        }
    }

    fun isOpenAppAdEnabled():Boolean = openAppEnabled

}