package com.example.channels.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.channels.ads.AdShownListener
import com.example.channels.ads.AdsManager
import com.yandex.mobile.ads.banner.BannerAdView


class AdsViewModel : ViewModel() {

    private lateinit var adsManager: AdsManager
    fun initializeAdsManager(context: Context) {
        if (!::adsManager.isInitialized) {
            adsManager = AdsManager(context)
        }
    }
    fun showInterstitialAd(listener: AdShownListener) {
        adsManager.showInterAd(listener)
    }
    fun showBannerAd() : BannerAdView? = adsManager.showBannerAd()

}