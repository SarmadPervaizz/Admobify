package com.sarmad.admobify.adsdk.utils


/** Maintain state of Interstitial whether
 * its showing or not **/

private var isShowingInterstitialAd = false

internal fun setShowingInterAd(value: Boolean) {
    isShowingInterstitialAd = value
}

internal fun isShowingInterAd(): Boolean {
    return isShowingInterstitialAd
}



/** Maintain state of Open Ad whether
 * its showing or not **/

private var isOpenAdShowing = false

internal fun setShowingOpenAd(value: Boolean) {
    isOpenAdShowing = value
}

internal fun isShowingOpenAd(): Boolean {
    return isOpenAdShowing
}



/** Maintain state of Reward Ad whether
 * its showing or not **/

private var isRewardAdShowing = false

internal fun setShowingRewardAd(value: Boolean) {
    isOpenAdShowing = value
}

internal fun isShowingRewardAd(): Boolean {
    return isOpenAdShowing
}
