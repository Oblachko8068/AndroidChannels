package com.example.channels.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.channels.R
import com.example.channels.ads.instreamAds.ContentVideoPlayer
import com.example.channels.ads.instreamAds.SampleInstreamAdPlayer
import com.example.channels.ads.instreamAds.SamplePlayer
import com.example.channels.databinding.FragmentVideoAdsBinding
import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdBinder
import com.yandex.mobile.ads.instream.InstreamAdListener
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration
import com.yandex.mobile.ads.instream.player.content.VideoPlayer
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener

interface InsteamAdClose {
    fun instreamAdCloseGoBack()
}

class VideoAdsFragment(
    private val instreamAd: InstreamAd
) : Fragment(){

    private lateinit var binding: FragmentVideoAdsBinding
    private var instreamAdBinder: InstreamAdBinder? = null
    private var instreamAdPlayer: SampleInstreamAdPlayer? = null
    private var contentVideoPlayer: ContentVideoPlayer? = null
    private var activePlayer: SamplePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayer()
        showInstreamAd()
    }

    override fun onPause() {
        super.onPause()
        if (instreamAdPlayer?.isPlaying() == true || contentVideoPlayer?.isPlaying() == true) {
            activePlayer = contentVideoPlayer?.takeIf { it.isPlaying() } ?: instreamAdPlayer
            activePlayer?.pause()
        } else {
            activePlayer = null
        }
    }

    override fun onResume() {
        super.onResume()
        activePlayer?.resume()
    }

    override fun onDestroy() {
        instreamAdBinder?.unbind()
        instreamAdBinder?.invalidateAdPlayer()
        instreamAdBinder?.invalidateVideoPlayer()

        contentVideoPlayer?.release()
        instreamAdPlayer?.release()
        activePlayer = null
        contentVideoPlayer = null
        instreamAdPlayer = null
        super.onDestroy()
    }

    private fun initPlayer(){
        val contentStreamUrl = getString(R.string.instream_content_url)
        contentVideoPlayer = ContentVideoPlayer(contentStreamUrl, binding.adPlayerView, object : InsteamAdClose{
            override fun instreamAdCloseGoBack() {
                back()
            }
        })
        instreamAdPlayer = SampleInstreamAdPlayer(binding.adPlayerView)
    }
    private fun back(){
        navigator().goBack()
    }

    private fun showInstreamAd() {
        instreamAdBinder = InstreamAdBinder(
            requireActivity().applicationContext,
            instreamAd,
            checkNotNull(instreamAdPlayer),
            checkNotNull(contentVideoPlayer)
        )
        instreamAdBinder?.apply {
            //setInstreamAdListener(eventLogger)
            bind(binding.instreamAdView)
        }
    }
}
