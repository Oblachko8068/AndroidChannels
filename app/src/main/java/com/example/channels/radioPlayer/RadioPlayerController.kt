package com.example.channels.radioPlayer

interface RadioPlayerController {

    val isPlaying: Boolean

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()
}