package com.example.channels

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import com.example.channels.ads.AdShownListener
import com.example.channels.ads.AdsManager
import com.example.channels.databinding.ActivityMainBinding
import com.example.channels.fragments.ExoPlayerFragment
import com.example.channels.fragments.MainFragment
import com.example.channels.fragments.Navigator
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding
    private lateinit var player: ExoPlayer
    private lateinit var adsManager: AdsManager

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adsManager = AdsManager(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MainFragment())
                .commit()
        }
    }

    override fun showVideoPlayerFragment(channel: Channel, selectedEpgDb: Epg?) {
        adsManager.showInterAd(object : AdShownListener {
            override fun onAdLoadedAndShown() {
                launchFragment(ExoPlayerFragment.newInstance(channel, selectedEpgDb))
            }
        })
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun onResume() {
        super.onResume()
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
        val instreamAdRequestConfiguration = InstreamAdRequestConfiguration.Builder("demo").build()
        val yandexAdsLoader: YandexAdsLoader = YandexAdsLoader(this, instreamAdRequestConfiguration)
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdViewProvider(binding.playerView)
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        binding.playerView.player = player
        yandexAdsLoader.setPlayer(player)
        val contentVideoUrl = getString(R.string.content_url_for_instream_ad)
        val mediaItem = MediaItem.Builder()
            .setUri(contentVideoUrl)
            .setAdTagUri(YandexAdsLoader.AD_TAG_URI)
    }*/
}

