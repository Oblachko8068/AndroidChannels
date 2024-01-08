package com.example.channels.ads.bannerAds

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

class YandexBannerAd(private val context: Context, private val adUnitId: String) : BannerAdProvider {

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

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.e(
                    "BannerAdErrorYandex",
                    "Failed to load banner ad. Error code: ${p0.code}, Message: ${p0.description}"
                )
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
        bannerAd?.setAdUnitId(adUnitId)
        bannerAd?.setAdSize(BannerAdSize.inlineSize(context, 600, 50))
        bannerAd?.loadAd(AdRequest.Builder().build())

    }

    override fun showBannerAd(): BannerAdView? {
        return bannerAd
    }

    override fun isAdLoaded(): Boolean = bannerReady

}