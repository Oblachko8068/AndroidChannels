package com.example.channels.fragments

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentExoplayerBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg

class ExoPlayerFragment: Fragment(), Player.Listener {
    val mp4 = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    private lateinit var mainBinding: FragmentExoplayerBinding
    private lateinit var player: ExoPlayer
    private var playbackPosition: Long = 0
    private var playbackState: Int = Player.STATE_IDLE
    private var channelStream =
        "https://cdn-cache01.voka.tv/live/5117.m3u8"


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mainBinding = FragmentExoplayerBinding.inflate(inflater, container, false)
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
        activity?.let { WindowInsetsControllerCompat(it.window, mainBinding.container) }.let {
            it?.hide(WindowInsetsCompat.Type.statusBars())
            it?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding = CustomControllerBinding.inflate(layoutInflater)
        val channel = arguments?.getSerializable("channel_exo_data") as? Channel
        val epg = arguments?.getSerializable("epg_exo_data") as? Epg
        if (channel != null) {
            val channelName = channel.name
            val channelDescription = epg?.title
            val channelIconResource = channel.image
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = epg?.timestart
            val channelTimestop = epg?.timestop
            val activeChannelName = view.findViewById<TextView>(R.id.activeChannelName1)
            activeChannelName.text = channelName
            val activeChannelDesc = view.findViewById<TextView>(R.id.activeChannelDesc1)
            activeChannelDesc.text = "$channelDescription"
            val activeChannelIcon = view.findViewById<ImageView>(R.id.activeChannelIcon1)
            //запись иконки
            context?.let {
                Glide.with(it)
                    .load(channelIconResource)
                    .into(activeChannelIcon)
            }
        }
        val backToMain = view.findViewById<ImageButton>(R.id.backToMain1)
        backToMain.setOnClickListener {
            navigator().goBack()
        }
        val settings = view.findViewById<ImageButton>(R.id.settings1)
        settings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), settings)
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                playbackPosition = player.currentPosition

                channelStream = when (item.itemId) {
                    R.id.action_setting1 -> mp4
                    R.id.action_setting2 -> mp4
                    R.id.action_setting3 -> mp4
                    else -> mp4
                }
                updateVideoView()
                true
            }
            popupMenu.show()
        }

        setupPlayer()
        player.addMediaItem(MediaItem.fromUri(mp4))
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playbackPosition")
            playbackState = savedInstanceState.getInt("playbackState")
            if (playbackState == Player.STATE_READY) {
                player.seekTo(playbackPosition)
                player.play()
            }
        }
    }

    private fun updateVideoView() {
        player.addMediaItem(MediaItem.fromUri(mp4))
        player.seekTo(playbackPosition)
        player.play()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        mainBinding.exoplayerView.player = player
        player.addListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition)
        outState.putInt("playbackState", playbackState)
    }
    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            playbackPosition = player.currentPosition
            playbackState = player.playbackState
            player.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        if (playbackState == Player.STATE_READY) {
            player.seekTo(playbackPosition)
            player.play()
        }
    }
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState){
            Player.STATE_BUFFERING -> {
                mainBinding.buffering.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                mainBinding.buffering.visibility = View.INVISIBLE
            }
        }
    }
    override fun onStop() {
        super.onStop()
        player.stop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
        activity?.let { WindowInsetsControllerCompat(it.window, mainBinding.container) }?.show(WindowInsetsCompat.Type.systemBars())
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    companion object {
        @JvmStatic
        private val channel_data = "channel_exo_data"
        @JvmStatic
        private val epg_data = "epg_exo_data"

        @JvmStatic
        fun newInstance(channel: Channel, selectedEpgDb: Epg?): ExoPlayerFragment {
            val args = Bundle()
            args.putSerializable(channel_data, channel)
            args.putSerializable(epg_data, selectedEpgDb)
            val fragment = ExoPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}


