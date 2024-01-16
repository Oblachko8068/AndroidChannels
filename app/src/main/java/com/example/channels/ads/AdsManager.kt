package com.example.channels.ads

import android.content.Context
import com.example.channels.ads.bannerAds.YandexBannerAd
import com.example.channels.ads.instreamAds.YandexInstreamAd
import com.example.channels.ads.interstitialAds.YandexInterstitialAd
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.instream.InstreamAd

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
        initializeInstreamAdInstances()
    }

    private fun initializeInstreamAdInstances() {
        adInstancesList.add(YandexInstreamAd(context))
        adInstancesList.filterIsInstance<YandexInstreamAd>().forEach { it.loadInstreamAd() }
    }

    private fun initializeBannerAdInstances() {
        adBannerIdList.forEach {
            adInstancesList.add(YandexBannerAd(context, it))
        }
        adInstancesList.filterIsInstance<YandexBannerAd>().forEach { it.loadBannerAd() }
    }

    private fun initializeInterstitialAdInstances() {
        adInterstitialIdList.forEach {
            adInstancesList.add(YandexInterstitialAd(context, it))
        }
        adInstancesList.filterIsInstance<YandexInterstitialAd>().forEach { it.loadInterAd() }
    }

    fun showBannerAd(): BannerAdView? {
        adInstancesList.forEach {
            if (it is YandexBannerAd && it.isAdLoaded()){
                updateInstancesList(it)
                return it.showBannerAd()
            }
        }
        return null
    }

    fun showInterOrInstreamAd(listener: AdShownListener): InstreamAd? {
        adInstancesList.forEach {ad ->
            if (ad is YandexInterstitialAd && ad.isAdLoaded()){
                ad.showInterAd(listener)
                ad.loadInterAd()
                updateInstancesList(ad)
                return null
            } else if (ad is YandexInstreamAd && ad.isAdLoaded()){
                updateInstancesList(ad)
                return ad.getInstreamAd(listener)
            }
        }
        return null
    }

    private fun updateInstancesList(ad: Any){
        adInstancesList.add(
            adInstancesList.removeAt(
                adInstancesList.indexOf(ad)
            )
        )
    }
}