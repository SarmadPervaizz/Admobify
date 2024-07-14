package com.sarmad.admobify.adsdk.howtouse

class HowToUseInterstitialAd {

    /**
    //Ads ID and Remote params required others are optional

    *   val adOptions = InterAdOptions().setAdId(getString(R.string.interstitial_ad)).
        setRemoteConfig(true).setFakeDelayForDialog(2).build(this)

    * Load Interstitial Ad callbacks are optional

     * InterstitialAdUtils(adOptions).loadInterstitialAd(object:InterAdLoadCallback(){
            override fun adAlreadyLoaded() {

            }

            override fun adLoaded() {

            }

            override fun adFailed(error: LoadAdError?, msg: String?) {

            }

            override fun adValidate() {

            }
        })

    * Show Interstitial Ad callbacks are optional

    *  InterstitialAdUtils(adOptions).showInterstitialAd(object:InterAdShowCallback(){
            override fun adNotAvailable() {

            }

            override fun adShowFullScreen() {

            }

            override fun adDismiss() {

            }

            override fun adFailedToShow() {

            }

            override fun adImpression() {

            }

            override fun adClicked() {

            }
        })

    * Load And Show interstitial Ad on Runtime both callbacks are optional

    *    InterstitialAdUtils(adOptions).loadAndShowInterAd(object : InterAdLoadCallback() {

            override fun adAlreadyLoaded() {

            }

            override fun adLoaded() {

            }

            override fun adFailed(error: LoadAdError?, msg: String?) {

            }

            override fun adValidate() {

            }
        },
            object : InterAdShowCallback() {
                override fun adNotAvailable() {
                    super.adNotAvailable()
                }

                override fun adShowFullScreen() {
                    super.adShowFullScreen()
                }

                override fun adDismiss() {
                    super.adDismiss()
                }

                override fun adFailedToShow() {
                    super.adFailedToShow()
                }

                override fun adImpression() {
                    super.adImpression()
                }

                override fun adClicked() {
                    super.adClicked()
                }
            })

    */



}