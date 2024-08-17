package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sarmad.admobify.adsdk.banner_ads.BannerAdType
import com.sarmad.admobify.adsdk.banner_ads.BannerAdUtils
import com.sarmad.admobify.adsdk.banner_ads.BannerCallback
import com.sarmad.adsdk.demo.databinding.ActivityBannerAdsBinding
import com.sarmad.adsdk.demo.databinding.ActivityBannerAdsNestedBinding

class DefaultBannerAdNestedActivity : AppCompatActivity() {

    private val binding:ActivityBannerAdsNestedBinding by lazy {
        ActivityBannerAdsNestedBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        BannerAdUtils(this).loadBannerAd(
            adId = getString(R.string.banner_ad),
            remote = true,
            container = binding.defaultContainer,
            adLoadingOrShimmer = null,
            adType = BannerAdType.DEFAULT_BANNER,
            callback = object : BannerCallback() {

            }
        )


    }
}