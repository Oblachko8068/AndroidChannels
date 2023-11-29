package com.example.channels.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.channels.ads.AdsManager


class AdsViewModel : ViewModel() {

    private lateinit var adsManager: AdsManager
    fun initializeAdsManager(context: Context) {
        if (!::adsManager.isInitialized) {
            adsManager = AdsManager(context)
        }
    }
    fun getAdsManager(): AdsManager {
        return adsManager
    }
}