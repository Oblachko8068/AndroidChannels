package com.example.channels.ads.bannerAds

import com.yandex.mobile.ads.banner.BannerAdView

interface BannerAdProvider {

    fun loadBannerAd()
    fun showBannerAd(): BannerAdView?
    fun isAdLoaded(): Boolean

}