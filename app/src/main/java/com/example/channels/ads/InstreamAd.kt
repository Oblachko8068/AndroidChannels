package com.example.channels.ads

import androidx.media3.ui.PlayerView
import com.example.channels.R
import com.yandex.mobile.ads.instream.InstreamAdBinder
import com.yandex.mobile.ads.instream.InstreamAdBreak
import com.yandex.mobile.ads.instream.InstreamAdListener
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.instream.player.ad.InstreamAdView
import com.yandex.mobile.ads.instream.player.content.VideoPlayer
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener
import com.yandex.mobile.ads.video.playback.model.VideoAd

/*class InstreamAd : com.yandex.mobile.ads.instream.InstreamAd{
    private var videoAd : InstreamAd? = null

    private fun showVideoAd(){
        val instreamAdView: InstreamAdView =
        val playerView: PlayerView = findViewById(R.id.player_view)
        val instreamAdPlayer = CustomInstreamAdPlayer()
        val contentVideoPlayer : VideoPlayer = CustomVideoPlayer()
        val instreamAdBinder = videoAd?.let {
            InstreamAdBinder(
                this,
                it,
                instreamAdPlayer,
                contentVideoPlayer
            )
        }
        instreamAdBinder?.setInstreamAdListener(object : InstreamAdListener {
            override fun onInstreamAdCompleted() {
                videoAd = null
                loadVideoAd()
                return
            }

            override fun onInstreamAdPrepared() {
                videoAd = null
                loadVideoAd()
                return
            }

            override fun onError(p0: String) {
                videoAd = null
                loadVideoAd()
                return
            }
        })
        instreamAdBinder?.prepareAd()
        instreamAdBinder?.bind(instreamAdView)
    }

    private fun loadVideoAd(){
        val instreamAdRequestConfiguration  = InstreamAdRequestConfiguration.Builder("demo-instream-vmap-yandex").build()
        InstreamAdLoader(this).apply {
            setInstreamAdLoadListener(object : InstreamAdLoadListener {
                override fun onInstreamAdLoaded(ad: InstreamAd) {
                    videoAd = ad
                }

                override fun onInstreamAdFailedToLoad(ad: String) {
                    videoAd = null
                }

            })
        }.loadInstreamAd(this, instreamAdRequestConfiguration)
    }

    override fun getAdBreaks(): MutableList<out InstreamAdBreak> {
        TODO("Not yet implemented")
    }
}

class CustomVideoPlayer : VideoPlayer {
    override fun getVolume(): Float {
        TODO("Not yet implemented")
    }

    override fun getVideoPosition(): Long {
        TODO("Not yet implemented")
    }

    override fun getVideoDuration(): Long {
        TODO("Not yet implemented")
    }

    override fun prepareVideo() {
        TODO("Not yet implemented")
    }

    override fun pauseVideo() {
        TODO("Not yet implemented")
    }

    override fun resumeVideo() {
        TODO("Not yet implemented")
    }

    override fun setVideoPlayerListener(p0: VideoPlayerListener?) {
        TODO("Not yet implemented")
    }

}
class CustomInstreamAdPlayer : InstreamAdPlayer {
    override fun prepareAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun playAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun pauseAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun resumeAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun stopAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun skipAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun setVolume(p0: VideoAd, p1: Float) {
        TODO("Not yet implemented")
    }

    override fun getVolume(p0: VideoAd): Float {
        TODO("Not yet implemented")
    }

    override fun releaseAd(p0: VideoAd) {
        TODO("Not yet implemented")
    }

    override fun setInstreamAdPlayerListener(p0: InstreamAdPlayerListener?) {
        TODO("Not yet implemented")
    }

    override fun getAdDuration(p0: VideoAd): Long {
        TODO("Not yet implemented")
    }

    override fun getAdPosition(p0: VideoAd): Long {
        TODO("Not yet implemented")
    }

    override fun isPlayingAd(p0: VideoAd): Boolean {
        TODO("Not yet implemented")
    }

}*/