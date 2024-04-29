package com.example.channels.musicPlayer

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaMetadata
import com.example.channels.R
import com.example.domain.model.Music

const val CHANNEL_ID = "MusicChannel"
const val NOTIFICATION_MUSIC_ID = 12

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
        createNotificationChannel()
        startForeground(NOTIFICATION_MUSIC_ID, createNotification())
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        musicPlayer = ExoPlayer.Builder(this)
            .build()
        musicPlayer.prepare()
        musicPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED){
                    playNext()
                }
            }
        })
    }

    override fun changeMediaItem(){
        val mediaItem = MediaItem.Builder()
            .setUri(musicListMA[currentMusicPosition].path)
            .build()
        musicPlayer.setMediaItem(mediaItem)
        musicPlayer.prepare()
    }

    fun playNewMusic(musicPosition: Int) {
        currentMusicPosition = musicPosition
        changeMediaItem()
        startPlayer()
    }

    fun getCurrentMusic(): Music = musicListMA[currentMusicPosition]

    private val ACTION_PLAY_PAUSE = "com.example.channels.musicPlayer.ACTION_PLAY_PAUSE"
    private val ACTION_PREVIOUS = "com.example.channels.musicPlayer.ACTION_PREVIOUS"
    private val ACTION_NEXT = "com.example.channels.musicPlayer.ACTION_NEXT"
    private val ACTION_STOP_SERVICE = "com.example.channels.musicPlayer.ACTION_STOP_SERVICE"

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MusicListFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSessionCompat.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.icon_dark_theme)
            .setContentTitle("Music-----")
            .setContentText("-----")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.icon_prev, "Previous", getPendingIntent(ACTION_PREVIOUS))
            .addAction(R.drawable.icon_pause, "Pause", getPendingIntent(ACTION_PLAY_PAUSE))
            .addAction(R.drawable.icon_next, "Next", getPendingIntent(ACTION_NEXT))
            .addAction(R.drawable.icon_exit, "Close", getPendingIntent(ACTION_STOP_SERVICE))
            .setStyle(mediaStyle)

        return notificationBuilder.build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_MUSIC_ID, createNotification())
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopForeground(true) // Остановить фоновое выполнение уведомления
                stopPlayer() // Остановить воспроизведение музыки
                stopSelf() // Остановить сервис
                return START_NOT_STICKY // Возвращаем START_NOT_STICKY, чтобы сервис не перезапускался автоматически после остановки
            }
            ACTION_PLAY_PAUSE -> {
                if (isPlaying) {
                    pausePlayer()
                } else {
                    startPlayer()
                }
            }
            ACTION_PREVIOUS -> {
                playPrevious()
            }
            ACTION_NEXT -> {
                playNext()
            }
            else -> return super.onStartCommand(intent, flags, startId)
        }
        return START_NOT_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
        stopForeground(true)
        mediaSessionCompat.release()
    }

    override val isPlaying: Boolean
        get() = musicPlayer.playWhenReady

    val currentMusicPositionLiveData: MutableLiveData<Int> = MutableLiveData()
    private var _currentMusicPosition: Int = 0
    override var currentMusicPosition: Int
        get() = _currentMusicPosition
        set(value) {
            _currentMusicPosition = value
            currentMusicPositionLiveData.postValue(value)
        }

    override var musicListMA: ArrayList<Music> = arrayListOf()

    override fun startPlayer() {
        musicPlayer.playWhenReady = true
    }

    override fun pausePlayer() {
        musicPlayer.playWhenReady = false
    }

    override fun stopPlayer() {
        musicPlayer.stop()
        stopSelf()
    }

    override fun playNext() {
        currentMusicPosition = (currentMusicPosition + 1) % musicListMA.size
        changeMediaItem()
    }

    override fun playPrevious() {
        currentMusicPosition = (currentMusicPosition - 1 + musicListMA.size) % musicListMA.size
        changeMediaItem()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
