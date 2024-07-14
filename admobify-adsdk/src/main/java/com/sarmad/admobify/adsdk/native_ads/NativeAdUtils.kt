package com.sarmad.admobify.adsdk.native_ads

import android.content.Context
import android.view.ViewGroup
import com.sarmad.admobify.adsdk.native_ads.ad_types.NativeAdType
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils

class NativeAdUtils {


    fun loadNativeAd(
        context: Context,
        adId: String,
        adContainer: ViewGroup?,
        model: NativeAdItemsModel?,
        callback: NativeAdCallback,
        adType: NativeAdType = NativeAdType.DEFAULT_AD
    ) {
        AdmobifyUtils.validateAdMobAdUnitId(adId)
        CustomNativeAd(context, adId, adContainer).loadNativeAd(
            adListener = callback,
            adType = adType,
            model = model
        )

    }

}