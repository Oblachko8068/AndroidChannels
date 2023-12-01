package com.example.channels.ads.bannerAds

import android.content.Context
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

const val adMyTargetBannerId = "demo-banner-mytarget"

class MyTargetBannerAd(val context: Context) {

    private var bannerAd: BannerAdView? = null

    fun loadBannerAd() {
        bannerAd = BannerAdView(context)
        bannerAd?.setAdUnitId(adMyTargetBannerId)
        bannerAd?.setAdSize(BannerAdSize.inlineSize(context, 600, 50))
        bannerAd?.loadAd(AdRequest.Builder().build())
    }

    fun showBannerAd(): BannerAdView? {
        bannerAd?.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(p0: AdRequestError) {

            }

            override fun onAdClicked() {

            }

            override fun onLeftApplication() {

            }

            override fun onReturnedToApplication() {

            }

            override fun onImpression(p0: ImpressionData?) {

            }
        })

        return bannerAd
    }

    fun isAdLoaded(): Boolean {
        return bannerAd != null
    }
}