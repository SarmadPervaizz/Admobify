package com.sarmad.adsdk.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.LoadAdError
import com.sarmad.admobify.adsdk.native_ads.NativeAdCallback
import com.sarmad.admobify.adsdk.native_ads.NativeAdItemsModel
import com.sarmad.admobify.adsdk.native_ads.NativeAdUtils
import com.sarmad.admobify.adsdk.native_ads.ad_types.NativeAdType
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.adsdk.demo.databinding.ActivityMainBinding
import com.sarmad.adsdk.demo.databinding.NativeAdLayoutBinding

class NativeAdsActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Admobify.setPremiumUser(true)

        loadDefaultNativeAd(true)
        loadIntroNativeAd(true)
        loadExitNativeAd(true)

    }


    private fun loadIntroNativeAd(remote:Boolean) {
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
                application = application,
                adId = getString(R.string.native_ad),
                adRemote = remote,
                adContainer = binding?.introNativeContainer ?: return,
                model = nativeAdModel,
                callback = object : NativeAdCallback() {
                    override fun adValidate() {
                        binding?.introNativeContainer?.visibility = View.INVISIBLE
                    }
                }, adType = NativeAdType.INTRO_SCREEN_AD
            )
        }

    }

    private fun loadDefaultNativeAd(remote:Boolean) {
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
                application,
                getString(R.string.native_ad),
                adRemote = remote,
                binding?.nativeContainer ?: return,
                nativeAdModel, object : NativeAdCallback() {

                    override fun adValidate() {
                        binding?.nativeContainer?.visibility = View.INVISIBLE
                    }


                }, NativeAdType.DEFAULT_AD
            )
        }

    }

    private fun loadExitNativeAd(remote:Boolean) {
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
                application,
                getString(R.string.native_ad),
                adRemote = remote,
                binding?.exitNativeContainer ?: return,
                nativeAdModel, object : NativeAdCallback() {

                    override fun adValidate() {
                        binding?.exitNativeContainer?.visibility = View.INVISIBLE
                    }

                }, NativeAdType.DEFAULT_AD
            )
        }

    }


}