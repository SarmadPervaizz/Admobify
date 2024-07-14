package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.intuit.sdp.BuildConfig
import com.sarmad.admobify.adsdk.app_open_ad.OpenAppAd
import com.sarmad.admobify.adsdk.banner_ads.BannerAdType
import com.sarmad.admobify.adsdk.banner_ads.BannerAdUtils
import com.sarmad.admobify.adsdk.banner_ads.BannerCallback
import com.sarmad.admobify.adsdk.consent_form.AdmobConsentForm
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdBuilder
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdOptions
import com.sarmad.admobify.adsdk.interstitial_ads.InterstitialAdUtils
import com.sarmad.admobify.adsdk.utils.Admobify
import com.sarmad.adsdk.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val adOptions = InterAdOptions().setAdId(getString(R.string.interstitial_ad)).
        setRemoteConfig(true).setFakeDelayForDialog(2).build(this)

        OpenAppAd().init(
            activity = this,
            adId = getString(R.string.open_app_ad),
            remote = true,
            preloadAd = false,
            reloadOnDismiss = true
        )

     /*   Admobify.initialize(
            context = this,
            testDevicesList = arrayListOf(""),
            premiumUser = false,
            buildFlavor = BuildConfig.BUILD_FLAVOR,
            buildVariant = BuildConfig.BUILD_VARIANT
        )*/

//        binding?.btnLoadShow?.setOnClickListener {
//            InterstitialAdUtils(adOptions).loadAndShowInterAd(null, null)
//        }

         AdmobConsentForm.getInstance().setDebug(BuildConfig.DEBUG)
             .setDeviceTestID("")
             .build(this)
             .loadAndShowConsentForm (onFormDismiss = {

             })

        /*binding?.nativeContainer?.viewTreeObserver?.addOnGlobalLayoutListener(object:ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                binding?.nativeContainer?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                val height = binding?.nativeContainer?.height

                val width = binding?.nativeContainer?.width

                Log.e("RectangleAd","$height $width")
            }
        })*/

    }
}