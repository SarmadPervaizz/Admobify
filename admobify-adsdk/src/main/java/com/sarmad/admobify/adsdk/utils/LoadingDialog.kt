package com.sarmad.admobify.adsdk.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
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
        if (customLayout!=null){
            setContentView(customLayout)
        } else {
            setContentView(R.layout.loading_dialog)
        }

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
            OpenAppAdState.disableOpenAppAd(LOG_TAG)
        }

        setOnDismissListener {
            OpenAppAdState.enableOpenAppAd(LOG_TAG)
        }

    }

}