package com.example.channels.ads.videoAds

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration

const val pageId = "demo-instream-vmap-yandex"

class InstreamAdManager(
    private val context: Context,
) {
    private var videoAd : InstreamAd? = null

    fun loadInstreamAd() {
        val instreamAdRequestConfiguration  = InstreamAdRequestConfiguration.Builder("demo-instream-vmap-yandex").build()
        InstreamAdLoader(context).apply {
            setInstreamAdLoadListener(object : InstreamAdLoadListener {
                override fun onInstreamAdLoaded(ad: InstreamAd) {
                    Log.e("VideoAdError", "noFailed to load video ad. Error code: ${ad}")
                    videoAd = ad
                }

                override fun onInstreamAdFailedToLoad(ad: String) {
                    Log.e("VideoAdError", "Failed to load video ad. Error code: ${ad}")
                    videoAd = null
                }

            })
        }.loadInstreamAd(context, instreamAdRequestConfiguration)
    }

    fun isAdLoaded(): Boolean {
        return videoAd != null
    }

    fun showVideoInstreamAd(): InstreamAd? {
        return videoAd
    }
}
