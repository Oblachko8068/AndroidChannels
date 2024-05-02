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
import androidx.core.app.NotificationCompat.Action
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.domain.model.Music

class MusicPlayerService : Service(), MusicPlayerController {

    lateinit var musicPlayer: ExoPlayer
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private val binder = MusicBinder()
    lateinit var musicListMA: List<Music>

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

    private fun createNotification(): Notification {
        val channelId = "MusicPlayerServiceChannel"
        val notificationIntent = Intent(this, MusicListFragment::class.java)
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

    val currentMusicPositionLiveData: MutableLiveData<Int> = MutableLiveData()
    private var _currentMusicPosition: Int = 0
    override var currentMusicPosition: Int
        get() = _currentMusicPosition
        set(value) {
            _currentMusicPosition = value
            currentMusicPositionLiveData.postValue(value)
        }

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
}