package com.sarmad.admobify.adsdk.howtouse

class HowToUseBannerAd {

    /**
     * DEFAULT BANNER AD Width match parent height 60dp
     *
     *           BannerAdUtils(this).loadBannerAd(
     *             adId = getString(R.string.banner_ad),
     *             remote = true,
     *             container = binding.container,
     *             adLoadingOrShimmer = null,
     *             adType = BannerAdType.DEFAULT_BANNER,
     *             callback = object : BannerCallback() {
     *              //override required callbacks
     *             }
     *         )
     */

    /**         RECTANGLE BANNER AD minimum width 300 dp and height 250 dp
     *           BannerAdUtils(this).loadBannerAd(
     *             adId = getString(R.string.banner_ad),
     *             remote = true,
     *             container = binding.container,
     *             adLoadingOrShimmer = null,
     *             adType = BannerAdType.RECTANGLE_BANNER,
     *             callback = object : BannerCallback() {
     *              //override required callbacks
     *             })
     */

    /**    COLLAPSIBLE BANNER AD width match parent and height 60 dp
     *           BannerAdUtils(this).loadBannerAd(
     *             adId = getString(R.string.banner_ad),
     *             remote = true,
     *             container = binding.container,
     *             adLoadingOrShimmer = null,
     *             adType = BannerAdType.COLLAPSIBLE_BANNER,
     *             callback = object : BannerCallback() {
     *              //override required callbacks
     *             })
     */

    /** INLINE ADAPTIVE BANNER AD width match parent and height custom
     *           BannerAdUtils(this).loadBannerAd(
     *             adId = getString(R.string.banner_ad),
     *             remote = true,
     *             container = binding.container,
     *             adLoadingOrShimmer = null,
     *             adType = BannerAdType.INLINE_ADAPTIVE_BANNER,
     *             callback = object : BannerCallback() {
     *              //override required callbacks
     *             },adaptiveBannerHeight = 200)
     */


}