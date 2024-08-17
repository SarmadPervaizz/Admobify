package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.LoadAdError
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdBuilder
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdLoadCallback
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdOptions
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdShowCallback
import com.sarmad.admobify.adsdk.interstitial_ads.InterstitialAdUtils
import com.sarmad.adsdk.demo.databinding.ActivityInterstitialAdsBinding
import com.sarmad.adsdk.demo.databinding.CustomLoadingDialogBinding

class InterstitialAdsActivity : AppCompatActivity() {

    private val binding: ActivityInterstitialAdsBinding by lazy {
        ActivityInterstitialAdsBinding.inflate(layoutInflater)
    }

    val adBuilder:InterAdBuilder get() {
        return InterAdOptions().setAdId(getString(R.string.interstitial_ad)).setRemoteConfig(true)
            .setFakeDelayForDialog(2)
            .setCustomLoadingLayout(CustomLoadingDialogBinding.inflate(layoutInflater).root)
            .setFullScreenLoading(true).build(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnLoadShow.setOnClickListener {
                InterstitialAdUtils(adBuilder).loadAndShowInterAd(null, null)
            }

            btnLoad.setOnClickListener {
                InterstitialAdUtils(adBuilder).loadInterstitialAd(null)
            }

            btnShow.setOnClickListener {
                InterstitialAdUtils(adBuilder).showInterstitialAd(null)
            }


        }
    }
}