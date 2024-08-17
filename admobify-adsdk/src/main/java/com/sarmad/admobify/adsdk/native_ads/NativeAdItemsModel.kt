package com.sarmad.admobify.adsdk.native_ads

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

data class NativeAdItemsModel(
    val nativeAdView:NativeAdView,
    val adHeadline: TextView,
    val adBody: TextView,
    val adIcon: ImageView?,
    val mediaView: MediaView?,
    val adCTA: AppCompatButton
)
