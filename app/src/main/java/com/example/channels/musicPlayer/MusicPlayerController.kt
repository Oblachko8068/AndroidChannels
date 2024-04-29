package com.example.channels.musicPlayer

import com.example.domain.model.Music

interface MusicPlayerController {

    val isPlaying: Boolean

    var currentMusicPosition: Int

    var musicListMA: ArrayList<Music>

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun changeMediaItem()

    fun playNext()

    fun playPrevious()
}