package com.sarmad.admobify.adsdk.banner_ads

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils

class BannerAdUtils(val activity: Activity) {

    fun loadBannerAd(
        adId: String,
        remote: Boolean,
        container: ViewGroup,
        adLoadingOrShimmer: View?,
        adType: BannerAdType,
        callback: BannerCallback,
        adaptiveBannerHeight: Int = 60
    ) {
        AdmobifyUtils.validateAdMobAdUnitId(adId)

        when (adType) {

            BannerAdType.DEFAULT_BANNER -> {

                BannerAdLoader.loadBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }


            BannerAdType.DEFAULT_BANNER_NEW_INSTANCE -> {

                BannerAdLoaderNew.loadBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }


            BannerAdType.RECTANGLE_BANNER -> {

                BannerAdLoader.loadRectangleBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }

            BannerAdType.RECTANGLE_BANNER_NEW_INSTANCE -> {

                BannerAdLoaderNew.loadRectangleBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }


            BannerAdType.COLLAPSIBLE_BANNER -> {

                BannerAdLoader.loadCollapsibleBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }

            BannerAdType.COLLAPSIBLE_BANNER_NEW_INSTANCE -> {

                BannerAdLoaderNew.loadCollapsibleBannerAd(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    callback = callback
                )

            }


            BannerAdType.INLINE_ADAPTIVE_BANNER -> {

                BannerAdLoader.loadInlineAdaptiveBanner(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    adaptiveBannerHeight = adaptiveBannerHeight,
                    callback = callback
                )

            }

            BannerAdType.INLINE_ADAPTIVE_BANNER_NEW_INSTANCE -> {

                BannerAdLoaderNew.loadInlineAdaptiveBanner(
                    context = activity,
                    adId = adId,
                    remote = remote,
                    container = container,
                    loadingOrShimmer = adLoadingOrShimmer,
                    adaptiveBannerHeight = adaptiveBannerHeight,
                    callback = callback
                )

            }


        }

    }


}