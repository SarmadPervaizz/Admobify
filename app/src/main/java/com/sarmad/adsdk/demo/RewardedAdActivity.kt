package com.sarmad.adsdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sarmad.admobify.adsdk.reward_ad.RewardAdLoadCallback
import com.sarmad.admobify.adsdk.reward_ad.RewardAdShowCallback
import com.sarmad.admobify.adsdk.reward_ad.RewardedAdUtils
import com.sarmad.adsdk.demo.databinding.ActivityRewardedAdBinding

class RewardedAdActivity : AppCompatActivity() {

    val binding: ActivityRewardedAdBinding by lazy {
        ActivityRewardedAdBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnLoad.setOnClickListener {
                RewardedAdUtils().loadRewardedAd(
                    context = this@RewardedAdActivity,
                    remote = true,
                    adUnit = getString(R.string.rewarded_ad),
                    callback = object : RewardAdLoadCallback() {
                        //override required callbacks
                    })
            }

            btnLoad.setOnClickListener {
                RewardedAdUtils().showRewardedAd(
                    context = this@RewardedAdActivity,
                    remote = true,
                    callback = object : RewardAdShowCallback() {
                        //override required callbacks
                        override fun rewardEarned() {

                        }
                    })
            }


        }
    }
}