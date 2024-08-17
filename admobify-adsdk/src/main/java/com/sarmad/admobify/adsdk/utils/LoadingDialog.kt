package com.sarmad.admobify.adsdk.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import com.sarmad.admobify.adsdk.R
import com.sarmad.admobify.adsdk.app_open_ad.OpenAppAdState

private const val LOG_TAG = "LoadingDialog"

class LoadingDialog(
    val activity: Activity,
    private val customLayout: ViewGroup?,
    private val fullScreenDialog:Boolean=false
) : Dialog(activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            customLayout != null -> setContentView(customLayout)
            fullScreenDialog -> setContentView(R.layout.fullscreen_loading_dialog)
            else -> setContentView(R.layout.loading_dialog)
        }

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setCancelable(false)

        if (fullScreenDialog){
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        } else {
            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        setOnShowListener {
            OpenAppAdState.disable(LOG_TAG)
        }

        setOnDismissListener {
            OpenAppAdState.enable(LOG_TAG)
        }

    }

}