package com.example.channels.ads.videoAds

import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.instream.player.content.VideoPlayer
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener
import com.yandex.mobile.ads.video.playback.model.VideoAd

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

}