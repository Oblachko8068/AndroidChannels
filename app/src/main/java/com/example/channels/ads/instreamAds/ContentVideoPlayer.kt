package com.example.channels.ads.instreamAds

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.channels.fragments.InstreamAdClose
import com.yandex.mobile.ads.instream.player.content.VideoPlayer
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener

class ContentVideoPlayer(
    private val videoUrl: String,
    private val exoPlayerView: PlayerView,
    private val param: InstreamAdClose,
) : VideoPlayer, SamplePlayer {

    private val context = exoPlayerView.context
    private var player = ExoPlayer.Builder(context).build()
    private lateinit var videoTrackGroup: Tracks.Group
    private var tracksList = mutableListOf<Int>()
    private var videoPlayerListener: VideoPlayerListener? = null

    init {
        player.addListener(ContentPlayerEventsListener())
    }

    override fun isPlaying() = player.isPlaying

    override fun resume() {
        player.playWhenReady = true
    }

    override fun pause() {
        player.playWhenReady = false
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun prepareVideo() {
        val userAgent = Util.getUserAgent(context, context.packageName)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, userAgent)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
        player.apply {
            playWhenReady = false
            addListener(ContentPlayerPrepareListener())
            setMediaSource(mediaSource)
            prepare()
        }
    }

    override fun getVideoPosition() = player.currentPosition

    override fun getVideoDuration() = player.duration

    override fun getVolume() = player.volume

    override fun pauseVideo() {
        exoPlayerView.useController = false
        pause()
    }

    override fun resumeVideo() {
        param.instreamAdCloseGoBack()
        exoPlayerView.player = player
        exoPlayerView.useController = true
        resume()
    }

    override fun setVideoPlayerListener(playerListener: VideoPlayerListener?) {
        videoPlayerListener = playerListener
    }

    fun release() {
        player.release()
    }

    private inner class ContentPlayerEventsListener : Player.Listener {

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            for (trackGroup in tracks.groups) {
                if (trackGroup.type == 2) {
                    videoTrackGroup = trackGroup
                    for (i in 0 until trackGroup.length) {
                        if (!tracksList.contains(trackGroup.getTrackFormat(i).width)) {
                            tracksList.add(trackGroup.getTrackFormat(i).width)
                        }
                    }
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                videoPlayerListener?.onVideoResumed()
            } else {
                videoPlayerListener?.onVideoPaused()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                onVideoCompleted()
            }
        }

        private fun onVideoCompleted() {
            videoPlayerListener?.onVideoCompleted()
        }
    }

    private inner class ContentPlayerPrepareListener : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                videoPlayerListener?.onVideoPrepared()
                player.removeListener(this)
            }
        }
    }
}
