package com.example.channels.fragments.radioPlayer

interface RadioPlayerController {

    val isPlaying: Boolean

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

}