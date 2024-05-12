package com.example.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.data.model.fromMusicToMusicDbEntity
import com.example.data.room.MusicDao
import com.example.domain.model.Music
import com.example.domain.repository.MusicDownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class MusicDownloadRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    @ApplicationContext private val appContext: Context
) : MusicDownloadRepository {

    @SuppressLint("Range")
    override suspend fun fetchMusic() {
        val musicList = mutableListOf<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = appContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Unknown"
                    val idC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                            ?: "Unknown"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Unknown"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        musicList.add(music)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        musicDao.addAllMusicFromPhone(musicList.map { it.fromMusicToMusicDbEntity() })
    }
}