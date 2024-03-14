package com.example.channels.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.channels.ads.AdShownListener
import com.example.channels.ads.AdsManager
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.instream.InstreamAd

class AdsViewModel : ViewModel() {

    private lateinit var adsManager: AdsManager

    fun initializeAdsManager(context: Context) {
        if (!::adsManager.isInitialized) {
            adsManager = AdsManager(context)
        }
    }

    fun showInterOrInstreamAd(listener: AdShownListener): InstreamAd? =
        adsManager.showInterOrInstreamAd(listener)

    fun showBannerAd(): BannerAdView? = adsManager.showBannerAd()

}