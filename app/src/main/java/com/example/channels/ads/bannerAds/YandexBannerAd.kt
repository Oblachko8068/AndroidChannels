package com.example.channels.ads.bannerAds

import android.content.Context
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

const val adsMaxWidth = 600
const val adsMaxHeight = 50

class YandexBannerAd(
    private val context: Context,
    private val adUnitId: String
) : BannerAdProvider {

    private var bannerAd: BannerAdView? = null
    private var bannerReady = false

    override fun loadBannerAd() {
        bannerReady = false
        bannerAd?.destroy()
        bannerAd = BannerAdView(context)
        bannerAd?.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                bannerReady = true
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {}

            override fun onAdClicked() {}

            override fun onLeftApplication() {}

            override fun onReturnedToApplication() {}

            override fun onImpression(p0: ImpressionData?) {}  
        })
        bannerAd?.setAdUnitId(adUnitId)
        bannerAd?.setAdSize(BannerAdSize.inlineSize(context, adsMaxWidth, adsMaxHeight))
        bannerAd?.loadAd(AdRequest.Builder().build())
    }

    override fun showBannerAd(): BannerAdView? = bannerAd

    override fun isAdLoaded(): Boolean = bannerReady

}