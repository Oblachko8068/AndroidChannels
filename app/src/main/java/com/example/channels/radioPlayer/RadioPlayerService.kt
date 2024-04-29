package com.example.channels.radioPlayer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.example.channels.MainActivity
import com.example.channels.R

const val CHANNEL_ID = "RadioChannel"
const val NOTIFICATION_RADIO_ID = 1

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class RadioPlayerService: Service(), RadioPlayerController {

    private lateinit var radioPlayer: ExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val binder: IBinder = RadioBinder()
    private var currentRadioImage: String? = null

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
        startForeground(NOTIFICATION_RADIO_ID, createNotification())
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun initializePlayer() {
        radioPlayer = ExoPlayer.Builder(this)
            .build()
    }

    fun changeTheRadio(currentRadio: String, currentTitle: String, image: String) {
        currentRadioImage = image
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(currentTitle)
            .build()
        val mediaItem =
            MediaItem.Builder()
                .setUri(currentRadio)
                .setMediaMetadata(mediaMetadata)
                .build()
        radioPlayer.setMediaItem(mediaItem, true)
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
            Intent(this, MainActivity::class.java),
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
            NOTIFICATION_RADIO_ID,
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
        radioPlayer.release()
        stopForeground(true)
        mediaSessionCompat.release()
        playerNotificationManager.setPlayer(null)
    }

    @UnstableApi
    private inner class MediaDescriptionAdapter :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            player.currentMediaItem?.mediaMetadata?.title ?: "Загрузка"

        override fun createCurrentContentIntent(player: Player): PendingIntent? = PendingIntent.getActivity(
            this@RadioPlayerService,
            0,
            Intent(this@RadioPlayerService, RadioPlayerService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        override fun getCurrentContentText(player: Player): CharSequence? = null

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            if (currentRadioImage != null) {
                Glide.with(this@RadioPlayerService)
                    .asBitmap()
                    .load(currentRadioImage)
                    .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            callback.onBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                return null
            }
            return null
        }
    }
}