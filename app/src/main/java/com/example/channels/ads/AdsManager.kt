package com.example.channels.ads

import android.content.Context
import com.example.channels.ads.interstitialAds.AppLovinInterAd
import com.example.channels.ads.interstitialAds.MyTargerInterAd
import com.example.channels.ads.interstitialAds.YandexInterAd

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
    }

    fun showInterAd(listener: AdShownListener) {
        for (ad in interstitialAdInstanceList) {
            if (ad is YandexInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                val currentAd =
                    interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad))
                interstitialAdInstanceList.add(currentAd)
                break
            } else if (ad is MyTargerInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                val currentAd =
                    interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad))
                interstitialAdInstanceList.add(currentAd)
                break
            } else if (ad is AppLovinInterAd && ad.isAdLoaded()) {
                ad.showInterAd(listener)
                ad.loadInterAd()
                val currentAd =
                    interstitialAdInstanceList.removeAt(interstitialAdInstanceList.indexOf(ad))
                interstitialAdInstanceList.add(currentAd)
                break
            }
        }
    }
}