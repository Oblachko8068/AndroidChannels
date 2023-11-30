package com.example.channels.ads

import android.content.Context
import com.example.channels.ads.bannerAds.AppLovinBannerAd
import com.example.channels.ads.bannerAds.MyTargetBannerAd
import com.example.channels.ads.bannerAds.YandexBannerAd
import com.example.channels.ads.interstitialAds.AppLovinInterstitialAd
import com.example.channels.ads.interstitialAds.MyTargerInterstitialAd
import com.example.channels.ads.interstitialAds.YandexInterstitialAd
import com.yandex.mobile.ads.banner.BannerAdView

interface AdShownListener {
    fun onAdLoadedAndShown()
}

class AdsManager(val context: Context) {

    private var adInstancesList = mutableListOf<Any>()

    init {
        initializeInterstitialAdInstances()
        initializeBannerAdInstances()
    }

    private fun initializeBannerAdInstances() {
        val yandexBannerAd = YandexBannerAd(context)
        yandexBannerAd.loadBannerAd()
        adInstancesList.add(yandexBannerAd)
        val myTargetBannerAd = MyTargetBannerAd(context)
        myTargetBannerAd.loadBannerAd()
        adInstancesList.add(myTargetBannerAd)
        val appLovinBannerAd = AppLovinBannerAd(context)
        appLovinBannerAd.loadBannerAd()
        adInstancesList.add(appLovinBannerAd)
    }

    private fun initializeInterstitialAdInstances() {
        val yandexInterstitialAd = YandexInterstitialAd(context)
        yandexInterstitialAd.loadInterAd()
        adInstancesList.add(yandexInterstitialAd)
        val myTargetInterstitialAd = MyTargerInterstitialAd(context)
        myTargetInterstitialAd.loadInterAd()
        adInstancesList.add(myTargetInterstitialAd)
        val appLovinInterstitialAd = AppLovinInterstitialAd(context)
        appLovinInterstitialAd.loadInterAd()
        adInstancesList.add(appLovinInterstitialAd)
    }

    fun showBannerAd(): BannerAdView? {
        for (ad in adInstancesList) {
            if (ad is YandexBannerAd && ad.isAdLoaded()) {
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                val bannerAd = ad.showBannerAd()
                //ad.loadBannerAd()
                return bannerAd
            } else if (ad is MyTargetBannerAd && ad.isAdLoaded()) {
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                val bannerAd = ad.showBannerAd()
                //ad.loadBannerAd()
                return bannerAd
            } else if (ad is AppLovinBannerAd && ad.isAdLoaded()) {
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                val bannerAd = ad.showBannerAd()
                //ad.loadBannerAd()
                return bannerAd
            }
        }
        return null
    }

    fun showInterAd(listener: AdShownListener) {
        for (ad in adInstancesList) {
            if (ad is YandexInterstitialAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                break
            } else if (ad is MyTargerInterstitialAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                adInstancesList.add(
                    adInstancesList.removeAt(
                        adInstancesList.indexOf(ad)
                    )
                )
                break
            } else if (ad is AppLovinInterstitialAd && ad.isAdLoaded()) {
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