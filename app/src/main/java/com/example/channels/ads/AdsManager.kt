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
    private val adBannerIdList = listOf("demo-banner-yandex", "demo-banner-mytarget", "demo-banner-applovin")
    private val adInterstitialIdList = listOf("demo-interstitial-yandex", "demo-interstitial-mytarget", "demo-interstitial-applovin")

    init {
        initializeInterstitialAdInstances()
        initializeBannerAdInstances()
    }

    private fun initializeBannerAdInstances() {
        adBannerIdList.forEach {
            adInstancesList.add(YandexBannerAd(context, it))
        }
        adInstancesList.forEach {
            if (it is YandexBannerAd){
                it.loadBannerAd()
            }
        }
    }

    private fun initializeInterstitialAdInstances() {
        adInterstitialIdList.forEach {
            adInstancesList.add(YandexInterstitialAd(context, it))
        }
        adInstancesList.forEach {
            if (it is YandexInterstitialAd){
                it.loadInterAd()
            }
        }
    }

    fun showBannerAd(): BannerAdView? {
        adInstancesList.forEach {
            if (it is YandexBannerAd && it.isAdLoaded()){
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(it)
                    )
                )
                return it.showBannerAd()
            }
        }
        return null
    }

    fun showInterAd(listener: AdShownListener) {
        for (ad in adInstancesList) {
            if (ad is YandexInterstitialAd && ad.isAdLoaded()){
                ad.showInterAd(listener)
                ad.loadInterAd()
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                break
            }
        }
    }
}