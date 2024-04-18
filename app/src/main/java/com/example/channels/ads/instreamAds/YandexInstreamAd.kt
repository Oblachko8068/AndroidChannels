package com.example.channels.ads.instreamAds

import android.content.Context
import android.util.Log
import com.example.channels.ads.AdShownListener
import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdListener
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration

const val PAGE_ID = "demo-instream-vmap-yandex"

class YandexInstreamAd(private val context: Context) {

    private var instreamAdLoader: InstreamAdLoader? = null
    private val eventLogger = InstreamAdEventLogger()
    private var instreamAd: InstreamAd ?= null
    private var instreamAdListener: AdShownListener ?= null

    fun loadInstreamAd() {
        instreamAdLoader = InstreamAdLoader(context)
        instreamAdLoader?.setInstreamAdLoadListener(eventLogger)
        val configuration = InstreamAdRequestConfiguration.Builder(PAGE_ID).build()
        instreamAdLoader?.loadInstreamAd(context, configuration)
    }

    inner class InstreamAdEventLogger : InstreamAdLoadListener, InstreamAdListener {

        override fun onInstreamAdLoaded(ad: InstreamAd) {
            Log.e("InstreamAd", "Instream ad loaded")
            instreamAd = ad
        }

        override fun onInstreamAdFailedToLoad(error: String) {
            Log.e("InstreamAd", "Instream ad failed to load")
            instreamAdListener?.onAdLoadedAndShown()
        }

        override fun onInstreamAdCompleted() {
            Log.e("InstreamAd", "Instream ad completed")
            instreamAdListener?.onAdLoadedAndShown()
        }

        override fun onInstreamAdPrepared() {
            Log.e("InstreamAd", "Instream ad prepared")
        }

        override fun onError(error: String) {
            Log.e("InstreamAd", "Instream ad error:")
            instreamAdListener?.onAdLoadedAndShown()
        }
    }

    fun isAdLoaded(): Boolean = instreamAd != null

    fun getInstreamAd(listener: AdShownListener): InstreamAd? {
        instreamAdListener = listener
        return instreamAd
    }
}