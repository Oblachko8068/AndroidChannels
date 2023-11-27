package com.example.channels.ads.interstitialAds

import android.content.Context
import com.example.channels.ads.AdLoadListener
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader


const val adInterstitialId = "demo-interstitial-yandex"
class YandexInterAd(val context: Context) {

    //private var interstitialAd: InterstitialAd? = null
    fun loadInterAd(listener: AdLoadListener) {
        val adRequest = AdRequestConfiguration.Builder(adInterstitialId).build()
        InterstitialAdLoader(context).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    listener.onAdLoaded(ad)
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {

                }
            })
        }.loadAd(adRequest)
    }

    /*fun showInterAd(): InterstitialAd? {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    interstitialAd = null
                }

                override fun onAdFailedToShow(adError: AdError) {

                }

                override fun onAdDismissed() {
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null
                    loadInterAd()
                }

                override fun onAdClicked() {
                }

                override fun onAdImpression(impressionData: ImpressionData?) {

                }
            })
        }
        return interstitialAd
    }*/

}