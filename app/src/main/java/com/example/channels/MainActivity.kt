package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.ExoPlayerFragment
import com.example.channels.fragments.MainFragment
import com.example.channels.fragments.Navigator
import com.example.di.di.Di
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private var interstitialAd : InterstitialAd? = null
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
                .commit()
        }

    }

    private fun loadInterAd(){
        val adRequest = AdRequestConfiguration.Builder("demo-banner-yandex").build()
        InterstitialAdLoader(this).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    interstitialAd = null
                }
            })
        }.loadAd(adRequest)
    }
    private fun showInterAd(){
        if (interstitialAd != null){
            interstitialAd?.apply {
                setAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdShown() {
                        interstitialAd = null
                        loadInterAd()
                        return
                    }
                    override fun onAdFailedToShow(adError: AdError) {
                        interstitialAd = null
                        loadInterAd()
                        return
                    }
                    override fun onAdDismissed() {
                        interstitialAd = null
                        loadInterAd()
                        return
                    }
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                    }
                    override fun onAdImpression(impressionData: ImpressionData?) {
                        // Called when an impression is recorded for an ad.
                    }
                })
                show(this@MainActivity)
            }
        } else {
            return
        }
    }
    override fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?) {
        showInterAd()
        launchFragment(ExoPlayerFragment.newInstance(channel, selectedEpgDb))
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        loadInterAd()
    }
    @SuppressLint("CommitTransaction")
    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}