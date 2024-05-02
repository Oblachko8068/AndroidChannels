package com.example.channels.musicPlayer

interface MusicPlayerController {

    val isPlaying: Boolean

    var currentMusicPosition: Int

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun changeMediaItem()

    fun playNext()

    fun playPrevious()
}