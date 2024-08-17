package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.sarmad.admobify.adsdk.banner_ads.BannerAdType
import com.sarmad.admobify.adsdk.banner_ads.BannerAdUtils
import com.sarmad.admobify.adsdk.banner_ads.BannerCallback
import com.sarmad.adsdk.demo.databinding.ActivityBannerAdsBinding

class BannerAdsActivity : AppCompatActivity() {

    private val binding:ActivityBannerAdsBinding by lazy {
        ActivityBannerAdsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        BannerAdUtils(this).loadBannerAd(
            getString(R.string.banner_ad), true,
            binding?.adaptiveContainer ?: return,null, BannerAdType.INLINE_ADAPTIVE_BANNER,
            object : BannerCallback() {

            },100
        )



        BannerAdUtils(this).loadBannerAd(
            getString(R.string.banner_ad), true,
            binding.rectangleContainer ?: return,null, BannerAdType.RECTANGLE_BANNER,
            object : BannerCallback() {

            }
        )

        BannerAdUtils(this).loadBannerAd(
            adId = getString(R.string.banner_ad),
            remote = true,
            container = binding.defaultContainer,
            adLoadingOrShimmer = null,
            adType = BannerAdType.DEFAULT_BANNER,
            callback = object : BannerCallback() {

            },
            adaptiveBannerHeight = 200
        )

        BannerAdUtils(this).loadBannerAd(
            getString(R.string.banner_ad), true,
            binding?.collapsibleContainer ?: return,null, BannerAdType.COLLAPSIBLE_BANNER,
            object : BannerCallback() {

            }
        )


    }
}