package com.example.channels.ads

import android.content.Context
import com.example.channels.ads.bannerAds.AppLovinBannerAd
import com.example.channels.ads.bannerAds.MyTargetBannerAd
import com.example.channels.ads.bannerAds.YandexBannerAd
import com.example.channels.ads.interstitialAds.AppLovinInterAd
import com.example.channels.ads.interstitialAds.MyTargerInterAd
import com.example.channels.ads.interstitialAds.YandexInterAd
import com.yandex.mobile.ads.banner.BannerAdView

interface AdShownListener {
    fun onAdLoadedAndShown()
}

class AdsManager(val context: Context) {

    private var interstitialAdInstanceList = mutableListOf<Any>()

    init {
        val yandexAd = YandexInterAd(context)
        yandexAd.loadInterAd()
        interstitialAdInstanceList.add(yandexAd)
        val myTargetAd = MyTargerInterAd(context)
        myTargetAd.loadInterAd()
        interstitialAdInstanceList.add(myTargetAd)
        val appLovinAd = AppLovinInterAd(context)
        appLovinAd.loadInterAd()
        interstitialAdInstanceList.add(appLovinAd)


        val yandexBannerAd = YandexBannerAd(context)
        yandexBannerAd.loadBannerAd()
        interstitialAdInstanceList.add(yandexBannerAd)
        val myTargetBannerAd = MyTargetBannerAd(context)
        myTargetBannerAd.loadBannerAd()
        interstitialAdInstanceList.add(myTargetBannerAd)
        val appLovinBannerAd = AppLovinBannerAd(context)
        appLovinBannerAd.loadBannerAd()
        interstitialAdInstanceList.add(appLovinBannerAd)
    }

    fun showBannerAd(): BannerAdView? {
        for (ad in interstitialAdInstanceList) {
            if (ad is YandexBannerAd && ad.isAdLoaded()) {
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                val bannerAd = ad.showBannerAd()
                ad.loadBannerAd()
                return bannerAd
            } else if (ad is MyTargetBannerAd && ad.isAdLoaded()) {
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                val bannerAd = ad.showBannerAd()
                ad.loadBannerAd()
                return bannerAd
            } else if (ad is AppLovinBannerAd && ad.isAdLoaded()) {
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                val bannerAd = ad.showBannerAd()
                ad.loadBannerAd()
                return bannerAd
            }
        }
        return null
    }
    fun showInterAd(listener: AdShownListener) {
        for (ad in interstitialAdInstanceList) {
            if (ad is YandexInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                break
            } else if (ad is MyTargerInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                break
            } else if (ad is AppLovinInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                interstitialAdInstanceList.add(interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad)))
                break
            }
        }
    }
}