package com.example.channels.ads

import android.content.Context
import com.example.channels.adUnitId
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

class InterstitialAd(private val context: Context){

    private var interstitialAd: InterstitialAd? = null

    fun loadInterAd() {
        val adRequest = AdRequestConfiguration.Builder(adUnitId).build()
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

    fun showInterAd(): InterstitialAd? {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    interstitialAd = null
                    loadInterAd()
                }

                override fun onAdFailedToShow(adError: AdError) {
                    interstitialAd = null
                    loadInterAd()
                }

                override fun onAdDismissed() {
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null
                    loadInterAd()
                }

                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
            })

        }
        return interstitialAd
    }

}