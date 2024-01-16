package com.example.channels.ads.instreamAds

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.video.playback.model.VideoAd

class SampleVideoAdPlayer(
    private val videoAd: VideoAd,
    private val exoPlayerView: PlayerView,
) {

    private val context = exoPlayerView.context
    private val adPlayer = ExoPlayer.Builder(context).build().apply {
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
        val userAgent = Util.getUserAgent(context, context.packageName)
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(streamUrl))
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

    private inner class ExoPlayerEventsListener : Player.Listener {

        private var adStarted = false
        private var adPrepared = false
        private var bufferingInProgress = false

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) onResumePlayback() else onPausePlayback()
        }

        private fun onResumePlayback() {
            if (adStarted) adPlayerListener?.onAdResumed(videoAd) else adPlayerListener?.onAdStarted(videoAd)
            adStarted = true
        }

        private fun onPausePlayback() {
            adPlayerListener?.onAdPaused(videoAd)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    if (adPrepared.not()) onAdPrepared()
                    if (bufferingInProgress) onAdBufferingFinished()
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

        override fun onPlayerError(error: PlaybackException) {
            resetState()
        }

        fun resetState() {
            adStarted = false
            adPrepared = false
            bufferingInProgress = false
        }
    }
}
