package com.example.channels.viewModels

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.example.channels.ads.AdShownListener
import com.example.channels.ads.AdsManager
import com.yandex.mobile.ads.banner.BannerAdView

class AdsViewModel : ViewModel() {

    private lateinit var adsManager: AdsManager
    private var isAdAvailable = true

    fun initializeAdsManager(context: Context) {
        if (!::adsManager.isInitialized) {
            adsManager = AdsManager(context)
        }
    }

    fun showInterAd(listener: AdShownListener) {
        if (isAdAvailable) {
            adsManager.showInterstitialAd(listener)
            startAdTimeout()
        } else {
            listener.onAdLoadedAndShown()
        }
    }

    private fun startAdTimeout() {
        isAdAvailable = false
        Handler(Looper.getMainLooper()).postDelayed({
            isAdAvailable = true
        }, 60000)
    }

    fun showBannerAd(): BannerAdView? = adsManager.showBannerAd()

}