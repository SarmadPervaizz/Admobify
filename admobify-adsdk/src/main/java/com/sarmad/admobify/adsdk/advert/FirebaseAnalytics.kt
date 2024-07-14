package com.sarmad.admobify.adsdk.advert

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sarmad.admobify.adsdk.utils.Logger


fun firebaseAnalytics(eventId:String, eventName:String){
    try {
        val analytics = Firebase.analytics

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME,eventName)
        }

        analytics.logEvent(eventId,bundle)
    }catch (e:Exception){
        Logger.logError("Analytics","error logging event:${e.message}")
    }
}