package com.example.channels.musicPlayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.domain.model.Music

class MusicPlayerService : Service(), MusicPlayerController {

    private lateinit var musicPlayer: ExoPlayer
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        val service: MusicPlayerService
            get() = this@MusicPlayerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        mediaSessionCompat = MediaSessionCompat(this, "MusicPlayerService")
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(this)
        musicPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        musicPlayer.prepare()
    }

    private fun createNotification(): Notification {
        val channelId = "MusicPlayerServiceChannel"
        val notificationIntent = Intent(this, MusicPlayerFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Music Player")
            .setContentText("Now Playing...")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Player Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSessionCompat.release()
    }

    override val isPlaying: Boolean
        get() = musicPlayer.playWhenReady

    override fun startPlayer() {
        if (!musicPlayer.isPlaying) {
            musicPlayer.playWhenReady = true
            startForeground(1, createNotification())
        }
    }

    override fun pausePlayer() {
        if (musicPlayer.isPlaying) {
            musicPlayer.playWhenReady = false
            startForeground(1, createNotification())
        }
    }

    override fun stopPlayer() {
        musicPlayer.stop()
        stopSelf()
    }

    override fun playNext() {
        TODO("Not yet implemented")
    }

    override fun playPrevious() {
        TODO("Not yet implemented")
    }
}