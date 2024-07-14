package com.sarmad.admobify.adsdk.reward_ad

import com.google.android.gms.ads.AdError

abstract class RewardAdShowCallback {

    abstract fun rewardEarned()

    open fun adValidate() {

    }

    open fun adNotAvailable() {

    }

    open fun adShowFullScreen() {

    }

    open fun adDismiss() {

    }

    open fun adFailedToShow(error: AdError?) {

    }

    open fun adImpression() {

    }

    open fun adClicked() {

    }


}