package com.sarmad.admobify.adsdk.consent_form

import android.app.Activity
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentDebugSettings.DebugGeography
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.sarmad.admobify.adsdk.utils.Logger

private const val TAG = "AdmobConsentForm"

class AdmobConsentForm private constructor(
    private val activity: Activity,
    private val consentBuilder: ConsentBuilder
) {


    companion object {
        fun getInstance(): ConsentBuilder {
            return ConsentBuilder()
        }
    }

    private var consentInformation: ConsentInformation? = null


    fun loadAndShowConsentForm(onFormDismiss: () -> Unit) {


        val params = if (consentBuilder.isDebug) {

            val debug = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(consentBuilder.debugGeography)
                .addTestDeviceHashedId(consentBuilder.deviceTestID).build()

            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(consentBuilder.tagUnderAgeConsent)
                .setConsentDebugSettings(debug).build()

        } else {

            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(consentBuilder.tagUnderAgeConsent).build()

        }

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation?.requestConsentInfoUpdate(activity, params,

            /** Consent Info Update Success */
            {
                Logger.logInfo(TAG, "Consent Info update success")

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                )
                /** Consent Form Dismiss */
                { error ->

                    if (error != null) {
                        val msg =
                            "Consent form dismiss error:${error.errorCode} msg:${error.message}"

                        Logger.logError(TAG, msg)
                    } else {
                        Logger.logInfo(TAG, "User consent received")
                    }

                    onFormDismiss.invoke()
                }
            })

        /** Consent Info Update Failure */
        { error ->
            Logger.logError(
                TAG,
                "Consent info update failure error:${error.errorCode} msg:${error.message}"
            )

            onFormDismiss.invoke()
        }
    }


    class ConsentBuilder() {

        var deviceTestID = ""

        var debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA

        var isDebug: Boolean = false

        var tagUnderAgeConsent: Boolean = false

        fun setDebug(value: Boolean): ConsentBuilder {
            isDebug = value
            return this
        }

        fun setDebugGeography(@DebugGeography value: Int): ConsentBuilder {
            debugGeography = value
            return this
        }

        fun setDeviceTestID(value: String): ConsentBuilder {
            deviceTestID = value
            return this
        }

        fun setTagForUnderAgeConsent(value: Boolean): ConsentBuilder {
            tagUnderAgeConsent = value
            return this
        }

        fun build(activity: Activity): AdmobConsentForm {
            return AdmobConsentForm(activity, this)
        }
    }
}