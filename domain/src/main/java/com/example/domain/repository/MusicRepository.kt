package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.Music

interface MusicRepository {

    fun getMusicList(): LiveData<List<Music>>

    fun addNewMusic(music: Music)
}