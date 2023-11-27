package com.example.channels.ads

import android.app.Activity
import android.content.Context
import com.example.channels.ads.interstitialAds.AppLovinInterAd
import com.example.channels.ads.interstitialAds.MyTargerInterAd
import com.example.channels.ads.interstitialAds.YandexInterAd
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener

interface AdLoadListener {
    fun onAdLoaded(ad: InterstitialAd)
}
interface AdShownListener {
    fun onAdLoadedAndShown()
}

class AdsManager(val context: Context) {
    private var interstitialAdList = mutableListOf<InterstitialAd>()

    init {
        val yandexAd = YandexInterAd(context)
        yandexAd.loadInterAd(object : AdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAdList.add(ad)
            }
        })
        val myTargetAd = MyTargerInterAd(context)
        myTargetAd.loadInterAd(object : AdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAdList.add(ad)
            }
        })
        val appLovinAd = AppLovinInterAd(context)
        appLovinAd.loadInterAd(object : AdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAdList.add(ad)
            }
        })
    }

    fun showInterAd(listener: AdShownListener) {
        if (interstitialAdList.isNotEmpty()) {
            val interstitialAd = interstitialAdList.removeAt(0)
            interstitialAd.apply {
                setAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdShown() {
                        listener.onAdLoadedAndShown()
                        interstitialAdList.add(interstitialAd)
                    }

                    override fun onAdFailedToShow(adError: AdError) {

                    }

                    override fun onAdDismissed() {

                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdImpression(impressionData: ImpressionData?) {

                    }
                })
            }.show(context as Activity)
        }
    }
}