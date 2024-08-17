package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.LoadAdError
import com.sarmad.admobify.adsdk.app_open_ad.OpenAppAd
import com.sarmad.admobify.adsdk.app_open_ad.SplashOpenAppAd
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdBuilder
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdLoadCallback
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdOptions
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdShowCallback
import com.sarmad.admobify.adsdk.interstitial_ads.InterstitialAdUtils
import com.sarmad.adsdk.demo.databinding.ActivityInterstitialAdsBinding
import com.sarmad.adsdk.demo.databinding.CustomLoadingDialogBinding

class SplashOpenAdActivity : AppCompatActivity() {

    private val binding: ActivityInterstitialAdsBinding by lazy {
        ActivityInterstitialAdsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.apply {

            btnLoadShow.text = "Load and Show open ad"
            btnLoad.text = "Load open ad"
            btnShow.text = "Show open ad"



            btnLoadShow.setOnClickListener {
                loadAndShow()
            }

            btnLoad.setOnClickListener {
                loadOpenAd()
            }

            btnShow.setOnClickListener {

                showOpenAd()
            }


        }
    }


    fun loadAndShow() {
        SplashOpenAppAd.loadOpenAppAd(this@SplashOpenAdActivity,
            getString(R.string.open_app_ad),
            remote = true,
            adLoaded = {
                SplashOpenAppAd.showOpenAppAd(
                    this@SplashOpenAdActivity,
                    adShowFullScreen = {},
                    adFailedToShow = {},
                    adDismiss = {})

            },
            adFailed = null,
            adValidate = {})
    }

    fun loadOpenAd() {
        SplashOpenAppAd.loadOpenAppAd(this@SplashOpenAdActivity,
            getString(R.string.open_app_ad),
            remote = true,
            adLoaded = {},
            adFailed = {},
            adValidate = {})
    }

    fun showOpenAd() {
        SplashOpenAppAd.showOpenAppAd(
            this@SplashOpenAdActivity,
            adShowFullScreen = {},
            adFailedToShow = {},
            adDismiss = {})

    }
}
