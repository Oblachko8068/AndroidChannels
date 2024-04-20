package com.example.channels.musicPlayer

interface MusicPlayerController {

    val isPlaying: Boolean

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()
    fun playMusic()
    fun playNext()
    fun playPrevious()

}