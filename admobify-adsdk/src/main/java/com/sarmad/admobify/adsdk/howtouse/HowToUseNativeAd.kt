package com.sarmad.admobify.adsdk.howtouse

class HowToUseNativeAd {


    /** currently 3 Native Ad Types available all of them
     * have separate native ad instance
     *
     *  NativeAdType.DEFAULT_AD , NativeAdType.INTRO_SCREEN_AD ,
     *  NativeAdType.EXIT_SCREEN_AD */

    /** Load and Show Native Ad
     *
     *
     *  val bind = NativeAdLayoutBinding.inflate(layoutInflater)
     *         bind.apply {
     *
     *             val nativeAdModel = NativeAdItemsModel(
     *                 root,
     *                 adHeadline = tvHeadline,
     *                 adBody = tvBody,
     *                 adIcon = appIcon,
     *                 mediaView = mediaView,
     *                 adCTA = adCTA
     *             )
     *
     *             NativeAdUtils().loadNativeAd(
     *                 context = this@NativeAdsActivity,
     *                 adId = getString(R.string.native_ad),
     *                 adRemote = remote,
     *                 adContainer = binding?.introNativeContainer ?: return,
     *                 model =nativeAdModel,
     *                 callback = object : NativeAdCallback() {
     *
     *                 }, adType = NativeAdType.INTRO_SCREEN_AD
     *             )
     *         }
     *
     */


    /** Pre-Cache Native Ad
     *   NativeAdUtils().loadNativeAd(
     *             context = this@NativeAdsActivity,
     *             adId = getString(R.string.native_ad),
     *             adContainer = null,
     *             model = null,
     *             callback = object : NativeAdCallback() {
     *
     *             }, adType = NativeAdType.INTRO_SCREEN_AD
     *         )
     *
     */

}