package com.example.channels.ads.interstitialAds

import com.example.channels.ads.AdShownListener

interface InterstitialAdProvider {

    fun loadInterAd()

    fun isAdLoaded(): Boolean

    fun showInterAd(listener: AdShownListener)
}