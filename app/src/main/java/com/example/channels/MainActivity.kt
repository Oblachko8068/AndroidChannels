package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ads.AdsLoader
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.ExoPlayerFragment
import com.example.channels.fragments.MainFragment
import com.example.channels.fragments.Navigator
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.channels.ads.InterstitialAd
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration
import com.yandex.mobile.ads.instream.exoplayer.YandexAdsLoader
import dagger.hilt.android.AndroidEntryPoint

const val adUnitId = "demo-banner-yandex"
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    private val interstitialAd = InterstitialAd(this)
    private lateinit var player: ExoPlayer
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
                .commit()
        }
    }

    override fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?) {
        interstitialAd.showInterAd()?.show(this)
        launchFragment(ExoPlayerFragment.newInstance(channel, selectedEpgDb))
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        interstitialAd.loadInterAd()
    }

    @SuppressLint("CommitTransaction")
    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    //видео реклама
    /*@SuppressLint("UnsafeOptInUsageError")
    private fun videoAd(){
        val instreamAdRequestConfiguration = InstreamAdRequestConfiguration.Builder(adUnitId).build()
        val yandexAdsLoader : YandexAdsLoader = YandexAdsLoader(this, instreamAdRequestConfiguration)
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdViewProvider(binding.playerView)
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        binding.playerView.player = player
        yandexAdsLoader.setPlayer(player)
    }*/
}

