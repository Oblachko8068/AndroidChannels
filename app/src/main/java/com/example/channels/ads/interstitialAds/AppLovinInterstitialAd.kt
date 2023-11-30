package com.example.channels.ads.interstitialAds

import android.app.Activity
import android.content.Context
import com.example.channels.ads.AdShownListener
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

const val adInterstitialIdAppLovin = "demo-interstitial-applovin"

class AppLovinInterstitialAd(private val context: Context) {

    var interstitialAd: InterstitialAd? = null
    fun loadInterAd() {
        val adRequest = AdRequestConfiguration.Builder(adInterstitialIdAppLovin).build()
        InterstitialAdLoader(context).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    interstitialAd = null
                }
            })
        }.loadAd(adRequest)
    }

    fun isAdLoaded(): Boolean {
        return interstitialAd != null
    }

    fun showInterAd(listener: AdShownListener) {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    interstitialAd?.setAdEventListener(null)
                    listener.onAdLoadedAndShown()
                }

                override fun onAdFailedToShow(adError: AdError) {
                    interstitialAd?.setAdEventListener(null)
                    listener.onAdLoadedAndShown()
                }

                override fun onAdDismissed() {
                    interstitialAd?.setAdEventListener(null)
                }

                override fun onAdClicked() {
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                }
            })
        }?.show(context as Activity)
    }
}