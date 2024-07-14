package com.sarmad.admobify.adsdk.advert

import android.app.Activity
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigurations {


    fun fetchRemotes(
        activity: Activity,
        jsonKey: String,
        defaultXml: Int,
        onSuccess: (String?) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {

        FirebaseApp.initializeApp(activity)

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaultXml)

        remoteConfig.fetchAndActivate().addOnCompleteListener {

            try {
                if (it.isSuccessful) {
                    val response = remoteConfig[jsonKey].asString()
                    onSuccess(response)
                } else {
                    onFailure(it.exception)
                }
            } catch (e: Exception) {
                onFailure(e)
            }

        }

    }

}