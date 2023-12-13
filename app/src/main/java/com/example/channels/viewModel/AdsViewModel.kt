package com.example.channels.viewModel

import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.example.channels.ads.AdShownListener
import com.example.channels.ads.AdsManager
import com.yandex.mobile.ads.banner.BannerAdView

class AdsViewModel : ViewModel() {

    private lateinit var adsManager: AdsManager
    private var isTimerOut = true
    private var timer: CountDownTimer? = null

    fun initializeAdsManager(context: Context) {
        if (!::adsManager.isInitialized) {
            adsManager = AdsManager(context)
        }
    }

    fun showInterstitialAd(listener: AdShownListener) {
        if (isTimerOut){
            adsManager.showInterAd(listener)
            startTimer()
        } else {
            listener.onAdLoadedAndShown()
        }
    }

    fun showBannerAd() : BannerAdView? = adsManager.showBannerAd()

    private fun startTimer() {
        isTimerOut = false
        timer = null
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                isTimerOut = true
            }
        }.start()
    }
}