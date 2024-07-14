package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sarmad.admobify.adsdk.native_ads.NativeAdCallback
import com.sarmad.admobify.adsdk.native_ads.NativeAdItemsModel
import com.sarmad.admobify.adsdk.native_ads.NativeAdUtils
import com.sarmad.admobify.adsdk.native_ads.ad_types.NativeAdType
import com.sarmad.adsdk.demo.databinding.ActivityMainBinding
import com.sarmad.adsdk.demo.databinding.NativeAdLayoutBinding

class NativeAdsActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        loadDefaultNativeAd()
        loadIntroNativeAd()
        loadExitNativeAd()

        NativeAdUtils().loadNativeAd(
            context = this@NativeAdsActivity,
            adId = getString(R.string.native_ad),
            adContainer = null,
            model = null,
            callback = object : NativeAdCallback() {

            }, adType = NativeAdType.INTRO_SCREEN_AD
        )

    }


    private fun loadIntroNativeAd() {
        val bind = NativeAdLayoutBinding.inflate(layoutInflater)
        bind.apply {

            val nativeAdModel = NativeAdItemsModel(
                root,
                adHeadline = tvHeadline,
                adBody = tvBody,
                adIcon = appIcon,
                mediaView = mediaView,
                adCTA = adCTA
            )

            NativeAdUtils().loadNativeAd(
                context = this@NativeAdsActivity,
                adId = getString(R.string.native_ad),
                adContainer = binding?.introNativeContainer ?: return,
                model = nativeAdModel,
                callback = object : NativeAdCallback() {

                }, adType = NativeAdType.INTRO_SCREEN_AD
            )
        }

    }

    private fun loadDefaultNativeAd() {
        val bind = NativeAdLayoutBinding.inflate(layoutInflater)
        bind.apply {

            val nativeAdModel = NativeAdItemsModel(
                root,
                adHeadline = tvHeadline,
                adBody = tvBody,
                adIcon = appIcon,
                mediaView = mediaView,
                adCTA = adCTA
            )

            NativeAdUtils().loadNativeAd(
                this@NativeAdsActivity,
                getString(R.string.native_ad),
                binding?.nativeContainer ?: return,
                nativeAdModel, object : NativeAdCallback() {

                }, NativeAdType.DEFAULT_AD
            )
        }

    }

    private fun loadExitNativeAd() {
        val bind = NativeAdLayoutBinding.inflate(layoutInflater)
        bind.apply {

            val nativeAdModel = NativeAdItemsModel(
                root,
                adHeadline = tvHeadline,
                adBody = tvBody,
                adIcon = appIcon,
                mediaView = mediaView,
                adCTA = adCTA
            )

            NativeAdUtils().loadNativeAd(
                this@NativeAdsActivity,
                getString(R.string.native_ad),
                binding?.exitNativeContainer ?: return,
                nativeAdModel, object : NativeAdCallback() {

                }, NativeAdType.EXIT_SCREEN_AD
            )
        }

    }


}