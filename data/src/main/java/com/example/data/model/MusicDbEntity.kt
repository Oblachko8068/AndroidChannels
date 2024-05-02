package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Music

@Entity(
    tableName = "music"
)

data class MusicDbEntity(
    @PrimaryKey val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long,
    val path: String,
    val artUri: String
) {
    fun toMusicDb(): Music {
        return Music(
            id = id,
            title = title,
            album = album,
            artist = artist,
            duration = duration,
            path = path,
            artUri = artUri
        )
    }
}

fun Music.fromMusicToMusicDbEntity(): MusicDbEntity = MusicDbEntity(
    id = this.id,
    title = this.title,
    album = this.album,
    artist = this.artist,
    duration = this.duration,
    path = this.path,
    artUri = this.artUri
)