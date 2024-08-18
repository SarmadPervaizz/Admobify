package com.sarmad.admobify.adsdk.native_ads

import android.app.Application
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.sarmad.admobify.adsdk.native_ads.ad_types.DefaultNativeAdLoader
import com.sarmad.admobify.adsdk.native_ads.ad_types.ExitNativeAdLoader
import com.sarmad.admobify.adsdk.native_ads.ad_types.IntroNativeAdLoader
import com.sarmad.admobify.adsdk.native_ads.ad_types.NativeAdType
import com.sarmad.admobify.adsdk.utils.AdmobifyUtils.hide

internal class CustomNativeAd(
    private val application: Application,
    private val adId: String,
    private val remote: Boolean,
    private val adContainer: ViewGroup?
) {

    fun loadNativeAd(
        model: NativeAdItemsModel?,
        adListener: NativeAdCallback,
        adType: NativeAdType
    ) {

        when (adType) {

            NativeAdType.DEFAULT_AD -> {
                DefaultNativeAdLoader.loadNativeAd(
                    application = application,
                    adId = adId,
                    remote = remote,
                    adListener = listenCallback(adListener, model)
                )
            }

            NativeAdType.INTRO_SCREEN_AD -> {
                IntroNativeAdLoader.loadNativeAd(
                    application = application,
                    adId = adId,
                    remote = remote,
                    adListener = listenCallback(adListener, model)
                )
            }

            NativeAdType.EXIT_SCREEN_AD -> {
                ExitNativeAdLoader.loadNativeAd(
                    application = application,
                    adId = adId,
                    remote = remote,
                    adListener = listenCallback(adListener, model)
                )
            }

        }

    }

    private fun listenCallback(
        adListener: NativeAdCallback,
        adItemsModel: NativeAdItemsModel?
    ): NativeAdCallback {
        val callback = object : NativeAdCallback() {

            override fun adLoaded(nativeAd: NativeAd?) {
                adItemsModel?.let {

                    if (nativeAd != null) {
                        populateNativeAd(it, nativeAd)
                        adContainer?.removeAllViews()
                        adContainer?.addView(adItemsModel.nativeAdView)
                    }

                }

                //both call backs invoked which ever
                // is listened in project

                adListener.adLoaded(nativeAd)
                adListener.adLoaded()
            }

            override fun adFailed(error: LoadAdError?) {
                adListener.adFailed(error)
            }

            override fun adImpression() {
                adListener.adImpression()
            }

            override fun adClicked() {
                adListener.adClicked()
            }

            override fun adValidate() {
                adListener.adValidate()
            }
        }
        return callback
    }

    private fun populateNativeAd(model: NativeAdItemsModel, nativeAd: NativeAd) {

        val nativeAdView = model.nativeAdView

        val tvHeadLine = model.adHeadline
        val tvBody = model.adBody
        val ctaButton = model.adCTA
        val adIcon = model.adIcon
        val mediaView = model.mediaView

        mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        tvHeadLine.text = nativeAd.headline
        nativeAdView.headlineView = tvHeadLine

        tvBody.text = nativeAd.body
        nativeAdView.bodyView = tvBody

        nativeAdView.callToActionView = ctaButton
        ctaButton.text = nativeAd.callToAction

        val image = nativeAd.icon

        if (image != null) {
            adIcon?.setImageDrawable(image.drawable)
            nativeAdView.iconView = adIcon
        } else {
            adIcon?.hide()
        }

        nativeAdView.mediaView = mediaView

        nativeAdView.setNativeAd(nativeAd)
    }

}