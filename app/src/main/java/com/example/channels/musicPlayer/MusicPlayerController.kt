package com.example.channels.musicPlayer

import com.example.domain.model.Music

interface MusicPlayerController {

    val isPlaying: Boolean

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun playNext()

    fun playPrevious()
}