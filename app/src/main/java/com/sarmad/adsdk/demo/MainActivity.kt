package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.datatransport.BuildConfig
import com.sarmad.admobify.adsdk.app_open_ad.OpenAppAd
import com.sarmad.admobify.adsdk.app_open_ad.OpenAppAdState
import com.sarmad.admobify.adsdk.consent_form.AdmobConsentForm
import com.sarmad.admobify.adsdk.interstitial_ads.InterAdOptions
import com.sarmad.adsdk.demo.databinding.ActivityMainNewBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainNewBinding? = null


    var disabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNewBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val adOptions = InterAdOptions().setAdId(getString(R.string.interstitial_ad)).
        setRemoteConfig(true).setLoadingDelayForDialog(2).build(this)

        OpenAppAd().init(
            application = application,
            adId = getString(R.string.open_app_ad),
            remote = true,
            preloadAd = false,
            loadOnPause = true,
            reloadOnDismiss = false
        )


        binding?.btnPurchase?.setOnClickListener {
            if (disabled){
                OpenAppAdState.enable("Main")
                disabled = false
                binding?.btnPurchase?.text = "enabled"
            } else {
                OpenAppAdState.disable("Main")
                disabled = true
                binding?.btnPurchase?.text = "disabled"
            }
        }

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