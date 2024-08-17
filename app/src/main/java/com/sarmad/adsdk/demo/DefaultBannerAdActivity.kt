package com.sarmad.adsdk.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sarmad.admobify.adsdk.banner_ads.BannerAdType
import com.sarmad.admobify.adsdk.banner_ads.BannerAdUtils
import com.sarmad.admobify.adsdk.banner_ads.BannerCallback
import com.sarmad.adsdk.demo.databinding.ActivityBannerAdsBinding
import com.sarmad.adsdk.demo.databinding.ActivityBannerAdsDefaultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DefaultBannerAdActivity : AppCompatActivity() {

    private val binding:ActivityBannerAdsDefaultBinding by lazy {
        ActivityBannerAdsDefaultBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {

            BannerAdUtils(this@DefaultBannerAdActivity).loadBannerAd(
                adId = getString(R.string.banner_ad),
                remote = true,
                container = binding.defaultContainer,
                adLoadingOrShimmer = null,
                adType = BannerAdType.DEFAULT_BANNER,
                callback = object : BannerCallback() {

                }
            )

            startActivity(Intent(this@DefaultBannerAdActivity,DefaultBannerAdNestedActivity::class.java))
        }


    }
}