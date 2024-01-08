package com.example.channels.ads.videoAds

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.video.playback.model.VideoAd

/*class MediaSourceCreator(
    private val context: Context,
) {
    @SuppressLint("UnsafeOptInUsageError")
    fun createMediaSource(streamUrl: String): MediaSource {
        val adMediaItem = MediaItem.fromUri(streamUrl)
        val userAgent = Util.getUserAgent(context, context.packageName)
        val defaultDataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        val adPlayerCacheFactory = CacheDataSource.Factory()
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)

        return DefaultMediaSourceFactory(adPlayerCacheFactory).createMediaSource(adMediaItem)
    }
}
class CustomVideoPlayer(
    private val videoAd: VideoAd,
    private val exoPlayerView: PlayerView,
) {

    private val mediaSourceCreator = MediaSourceCreator(exoPlayerView.context)

    private val adPlayer = ExoPlayer.Builder(exoPlayerView.context)
        .build().apply {
            addListener(ExoPlayerEventsListener())
        }

    private var adPlayerListener: InstreamAdPlayerListener? = null

    val adDuration: Long
        get() = adPlayer.duration

    val adPosition: Long
        get() = adPlayer.currentPosition

    val isPlayingAd: Boolean
        get() = adPlayer.isPlaying
    @SuppressLint("UnsafeOptInUsageError")
    fun prepareAd() {
        val streamUrl = videoAd.mediaFile.url
        val mediaSource = mediaSourceCreator.createMediaSource(streamUrl)
        adPlayer.apply {
            playWhenReady = false
            setMediaSource(mediaSource)
            prepare()
        }
    }

    fun playAd() {
        exoPlayerView.player = adPlayer
        resumeAd()
    }

    fun pauseAd() {
        adPlayer.playWhenReady = false
    }

    fun resumeAd() {
        adPlayer.playWhenReady = true
    }

    fun stopAd() {
        pauseAd()
        adPlayerListener?.onAdStopped(videoAd)
    }

    fun skipAd() {
        pauseAd()
        adPlayerListener?.onAdSkipped(videoAd)
    }

    fun setVolume(volume: Float) {
        adPlayer.volume = volume
    }

    fun getVolume() = adPlayer.volume


    fun releaseAd() {
        adPlayer.release()
    }

    fun setInstreamAdPlayerListener(instreamAdPlayerListener: InstreamAdPlayerListener?) {
        adPlayerListener = instreamAdPlayerListener
    }

    fun onDestroy() {
        adPlayer.release()
    }

    private inner class ExoPlayerEventsListener : androidx.media3.common.Player.Listener {

        private var adStarted = false
        private var adPrepared = false
        private var bufferingInProgress = false

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                onResumePlayback()
            } else {
                onPausePlayback()
            }
        }

        private fun onResumePlayback() {
            if (adStarted) {
                adPlayerListener?.onAdResumed(videoAd)
            } else {
                adPlayerListener?.onAdStarted(videoAd)
            }
            adStarted = true
        }

        private fun onPausePlayback() {
            adPlayerListener?.onAdPaused(videoAd)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    if (adPrepared.not()) {
                        onAdPrepared()
                    }
                    if (bufferingInProgress) {
                        onAdBufferingFinished()
                    }
                }

                Player.STATE_BUFFERING -> onAdBufferingStarted()
                Player.STATE_ENDED -> onEndedState()
            }
        }

        private fun onAdPrepared() {
            adPlayerListener?.onAdPrepared(videoAd)
        }

        private fun onEndedState() {
            resetState()
            adPlayerListener?.onAdCompleted(videoAd)
        }

        private fun onAdBufferingStarted() {
            bufferingInProgress = true
            adPlayerListener?.onAdBufferingStarted(videoAd)
        }

        private fun onAdBufferingFinished() {
            bufferingInProgress = false
            adPlayerListener?.onAdBufferingFinished(videoAd)
        }

        fun resetState() {
            adStarted = false
            adPrepared = false
            bufferingInProgress = false
        }
    }

}

interface SamplePlayer {

    fun isPlaying(): Boolean
    fun resume()
    fun pause()
}

class CustomInstreamAdPlayer(
    private val exoPlayerView: PlayerView,
) : InstreamAdPlayer, SamplePlayer {

    private val adPlayers = mutableMapOf<VideoAd, CustomVideoPlayer>()

    private var currentVideoAd: VideoAd? = null
    private var adPlayerListener: InstreamAdPlayerListener? = null

    override fun setInstreamAdPlayerListener(instreamAdPlayerListener: InstreamAdPlayerListener?) {
        adPlayerListener = instreamAdPlayerListener
        adPlayers.values.forEach { it.setInstreamAdPlayerListener(instreamAdPlayerListener) }
    }

    override fun prepareAd(videoAd: VideoAd) {
        val videoAdPlayer = CustomVideoPlayer(videoAd, exoPlayerView)
        adPlayers[videoAd] = videoAdPlayer

        videoAdPlayer.setInstreamAdPlayerListener(adPlayerListener)
        videoAdPlayer.prepareAd()
    }

    override fun playAd(videoAd: VideoAd) {
        currentVideoAd = videoAd
        adPlayers[videoAd]?.playAd()
    }

    override fun pauseAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.pauseAd()
    }

    override fun resumeAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.resumeAd()
    }

    override fun stopAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.stopAd()
    }

    override fun skipAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.skipAd()
    }

    override fun setVolume(videoAd: VideoAd, volume: Float) {
        adPlayers[videoAd]?.setVolume(volume)
    }

    override fun getVolume(videoAd: VideoAd): Float {
        return adPlayers[videoAd]?.getVolume() ?: DEFAULT_VOLUME
    }

    override fun getAdDuration(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adDuration ?: 0
    }

    override fun getAdPosition(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adPosition ?: 0
    }

    override fun isPlayingAd(videoAd: VideoAd): Boolean {
        return adPlayers[videoAd]?.isPlayingAd ?: false
    }

    override fun releaseAd(videoAd: VideoAd) {
        if (videoAd == currentVideoAd) {
            currentVideoAd = null
        }

        adPlayers[videoAd]?.let { videoAdPlayer ->
            videoAdPlayer.setInstreamAdPlayerListener(null)
            videoAdPlayer.releaseAd()
        }

        adPlayers.remove(videoAd)
    }

    fun release() {
        adPlayers.values.forEach(CustomVideoPlayer::releaseAd)
        currentVideoAd = null
    }

    override fun isPlaying(): Boolean {
        return adPlayers[currentVideoAd]?.isPlayingAd ?: false
    }

    override fun resume() {
        adPlayers[currentVideoAd]?.resumeAd()
    }

    override fun pause() {
        adPlayers[currentVideoAd]?.pauseAd()
    }

    private companion object {
        private const val DEFAULT_VOLUME = 0f
    }
}*/