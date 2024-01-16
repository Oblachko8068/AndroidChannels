package com.example.channels.fragments.radioPlayer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerNotificationManager

const val CHANNEL_ID = "RadioChannel"
const val NOTIFICATION_ID = 1
const val radioUri = "https://hls-01-radiorecord.hostingradio.ru/record/112/playlist.m3u8"

class RadioPlayerService : Service(), RadioPlayerController {

    private lateinit var radioPlayer: ExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val binder: IBinder = RadioBinder()

    inner class RadioBinder : Binder() {
        val service: RadioPlayerService
            get() = this@RadioPlayerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        createNotificationChannel()
        mediaSessionCompat = MediaSessionCompat(this, "RadioPlayerService")
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, RadioPlayerService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSessionCompat.setSessionActivity(pendingIntent)
        playerNotificationManager = createPlayerNotificationManager()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(false)
            .createMediaSource(MediaItem.fromUri(radioUri))
        val trackSelector = DefaultTrackSelector(this)
        radioPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        radioPlayer.addMediaSource(hlsMediaSource)
        radioPlayer.prepare()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Radio Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, RadioPlayerService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .build()
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun createPlayerNotificationManager(): PlayerNotificationManager =
        PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID,
            MediaDescriptionAdapter()
        ).build().apply {
            setMediaSessionToken(mediaSessionCompat.sessionToken)
            setPlayer(radioPlayer)
        }

    override val isPlaying: Boolean
        get() = radioPlayer.playWhenReady

    override fun startPlayer() {
        radioPlayer.playWhenReady = true
    }

    override fun pausePlayer() {
        radioPlayer.playWhenReady = false
    }

    override fun stopPlayer() {
        radioPlayer.stop()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        mediaSessionCompat.release()
    }

    private fun releasePlayer() {
        radioPlayer.release()
        stopForeground(true)
    }

    @UnstableApi
    private inner class MediaDescriptionAdapter :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            player.currentMediaItem?.mediaMetadata?.title ?: "Radio Record"

        override fun createCurrentContentIntent(player: Player): PendingIntent? = null

        override fun getCurrentContentText(player: Player): CharSequence =
            player.currentMediaItem?.mediaMetadata?.description ?: "Описание"

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? = null
    }
}
