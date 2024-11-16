package com.sarmad.admobify.adsdk.advert

import android.os.Bundle
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sarmad.admobify.adsdk.utils.logger.Category
import com.sarmad.admobify.adsdk.utils.logger.Level
import com.sarmad.admobify.adsdk.utils.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun firebaseAnalytics(@Size(min = 1, max = 40) eventId: String, eventName: String) {
    CoroutineScope(Dispatchers.IO).launch {

        try {
            val analytics = Firebase.analytics

            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, eventName)
            }

            analytics.logEvent(eventId, bundle)

        } catch (e: Exception) {
            Logger.log(Level.ERROR, Category.General, "error logging firebase event:${e.message}")
        }

    }
}