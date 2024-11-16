package com.sarmad.admobify.adsdk.interstitial_ads

import android.app.Activity
import android.view.ViewGroup

open class InterAdOptions {

    private var activity: Activity? = null

    private var reloadAdOnDismiss = false

    private var adID: String = ""

    private var remote = true

    private var dialogFakeDelay = 0L

    private var customLoadingLayout:ViewGroup?=null

    private var fullScreenDialog = false

    fun setRemoteConfig(value: Boolean): InterAdOptions {
        remote = value
        return this
    }

    fun isRemoteEnabled(): Boolean = remote


    /** To Show Loading Dialog with fake delay for 1 or 2 seconds */

    fun setLoadingDelayForDialog(seconds:Int):InterAdOptions{
        dialogFakeDelay = seconds * 1000L
        return this
    }

    val getDialogFakeDelay : Long get() = dialogFakeDelay

    fun setAdId(id: String): InterAdOptions {
        adID = id
        return this
    }

    fun getAdId(): String = adID

    fun setReloadOnDismiss(value: Boolean): InterAdOptions {
        reloadAdOnDismiss = value
        return this
    }

    fun canReloadOnDismiss(): Boolean = reloadAdOnDismiss

    fun setCustomLoadingLayout(layout:ViewGroup):InterAdOptions{
        customLoadingLayout = layout
        return this
    }

    fun getCustomLoadingLayout():ViewGroup?{
        return customLoadingLayout
    }

    fun setFullScreenLoading(value:Boolean):InterAdOptions{
        fullScreenDialog = value
        return this
    }

    fun isFullScreenLoading():Boolean = fullScreenDialog

    internal fun getActivity(): Activity? {
        return activity
    }

    fun build(activity: Activity): InterAdBuilder {
        this.activity = activity
        return InterAdBuilder(this)
    }


}
