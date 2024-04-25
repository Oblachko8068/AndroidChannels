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

class YandexInterstitialAd(
    private val context: Context,
    private val adUnitId: String
) : InterstitialAdProvider {

    private var interstitialAd: InterstitialAd? = null

    override fun loadInterAd() {
        val adRequest = AdRequestConfiguration.Builder(adUnitId).build()
        InterstitialAdLoader(context).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@YandexInterstitialAd.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    interstitialAd = null
                }
            })
        }.loadAd(adRequest)
    }

    override fun showInterAd(listener: AdShownListener) {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    listener.onAdLoadedAndShown()
                    interstitialAd?.setAdEventListener(null)
                }

                override fun onAdFailedToShow(adError: AdError) {
                    listener.onAdLoadedAndShown()
                    interstitialAd?.setAdEventListener(null)
                }

                override fun onAdDismissed() {}

                override fun onAdClicked() {}

                override fun onAdImpression(impressionData: ImpressionData?) {}
            })
        }?.show(context as Activity)
    }

    override fun isAdLoaded(): Boolean = interstitialAd != null
}