package com.example.channels.ads

import android.content.Context
import com.example.channels.ads.bannerAds.YandexBannerAd
import com.example.channels.ads.interstitialAds.YandexInterstitialAd
import com.yandex.mobile.ads.banner.BannerAdView

interface AdShownListener {

    fun onAdLoadedAndShown()
}

class AdsManager(val context: Context) {

    private var adInstancesList = mutableListOf<Any>()
    private val adBannerIdList =
        listOf("demo-banner-yandex", "demo-banner-mytarget", "demo-banner-applovin")
    private val adInterstitialIdList = listOf(
        "demo-interstitial-yandex",
        "demo-interstitial-mytarget",
        "demo-interstitial-applovin"
    )

    init {
        initializeAdsInstances()
    }

    private fun initializeAdsInstances() {
        adBannerIdList.forEach { adInstancesList.add(YandexBannerAd(context, it)) }
        adInterstitialIdList.forEach { adInstancesList.add(YandexInterstitialAd(context, it)) }
        adInstancesList.filterIsInstance<YandexBannerAd>().forEach { it.loadBannerAd() }
        adInstancesList.filterIsInstance<YandexInterstitialAd>().forEach { it.loadInterAd() }
    }

    fun showBannerAd(): BannerAdView? {
        adInstancesList.forEach {
            if (it is YandexBannerAd && it.isAdLoaded()) {
                updateInstancesList(it)
                val bannerAd = it.showBannerAd()
                it.loadBannerAd()
                return bannerAd
            }
        }
        return null
    }

    fun showInterstitialAd(listener: AdShownListener) {
        for (i in 0 until adInstancesList.size) {
            val ad = adInstancesList[i]
            if (ad is YandexInterstitialAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                updateInstancesList(ad)
                break
            }
            if (i == adInstancesList.size - 1) listener.onAdLoadedAndShown()
        }
    }

    private fun updateInstancesList(ad: Any) {
        adInstancesList.add(
            adInstancesList.removeAt(
                adInstancesList.indexOf(ad)
            )
        )
    }
}