package com.example.channels

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.channels.databinding.ActivityInterstitialAdBinding
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

class InterstitialAdActivity : AppCompatActivity(R.layout.activity_interstitial_ad) {
    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private lateinit var binding: ActivityInterstitialAdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterstitialAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Interstitial ads loading should occur after initialization of the SDK.
        // Initialize SDK as early as possible, for example in Application.onCreate or Activity.onCreate
        interstitialAdLoader = InterstitialAdLoader(this).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    // The ad was loaded successfully. Now you can show loaded ad.
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    // Ad failed to load with AdRequestError.
                    // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                }
            })
        }
        loadInterstitialAd()
    }

     fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder("your-ad-unit-id").build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }
    fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    // Called when ad is shown.
                }
                override fun onAdFailedToShow(adError: AdError) {
                    // Called when an InterstitialAd failed to show.
                }
                override fun onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }
                override fun onAdImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
            })
            show(this@InterstitialAdActivity)
        }
    }

}