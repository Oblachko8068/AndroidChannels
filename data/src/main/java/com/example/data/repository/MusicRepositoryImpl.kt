package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.model.fromMusicToMusicDbEntity
import com.example.data.room.MusicDao
import com.example.domain.model.Music
import com.example.domain.repository.MusicRepository
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao
): MusicRepository {

    override fun getMusicList(): LiveData<List<Music>> = musicDao.getMusicListAll().map { musicDbEntities -> musicDbEntities.map { it.toMusicDb() } }

    override fun addNewMusic(music: Music) {
        musicDao.addNewMusic(music.fromMusicToMusicDbEntity())
    }
}