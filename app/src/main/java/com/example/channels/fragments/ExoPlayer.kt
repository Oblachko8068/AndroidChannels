package com.example.channels.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParserFactory
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentExoplayerBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val channel_data = "channel_exo_data"
const val epg_data = "epg_exo_data"
const val hlsUri = "https://cdn-cache01.voka.tv/live/5117.m3u8"

class ExoPlayerFragment : Fragment(), Player.Listener {

    private var visibilityView: Boolean = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: FragmentExoplayerBinding
    private lateinit var player: ExoPlayer
    private var playbackPosition: Long = 0
    private var playbackState: Int = Player.STATE_IDLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExoplayerBinding.inflate(inflater, container, false)

        hideSystemUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val channel = arguments?.getSerializable("channel_exo_data") as? Channel
        val epg = arguments?.getSerializable("epg_exo_data") as? Epg
        if (channel != null) {
            val channelName = channel.name
            val channelDescription = epg?.title
            val channelIconResource = channel.image
            //val channelStream = extras.getString("channel_stream")
            val channelTimestart = epg?.timestart
            val channelTimestop = epg?.timestop
            binding.activeChannelName.text = channelName
            binding.activeChannelDesc.text = "$channelDescription"
            //запись иконки
            context?.let {
                Glide.with(it)
                    .load(channelIconResource)
                    .into(binding.activeChannelIcon)
            }
            val totalTime = 60
            val channelTimestart1 = System.currentTimeMillis() / 1000
            //updateProgressBar(totalTime, channelTimestart1)

            binding.textViewTimeToTheEnd.text = totalTime.toString()
        }
        binding.backToMain.setOnClickListener {
            navigator().goBack()
        }
        binding.container.setOnClickListener {
            if (player.isPlaying) {
                coroutineScope.launch {
                    if (visibilityView) {
                        hideOtherViews()
                        visibilityView = false
                    } else {
                        showOtherViews()
                        delay(3000)
                        hideOtherViews()
                    }
                }
            }
        }

        binding.settings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.settings)
            popupMenu.menuInflater.inflate(R.menu.menu_settings, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                /*channelStream = when (item.itemId) {
                    R.id.action_setting1 ->
                    R.id.action_setting2 ->
                    R.id.action_setting3 ->
                    // Добавьте обработку других пунктов меню для других качеств видео
                    else -> channelStream
                }
                updateVideoView()*/
                true
            }
            popupMenu.show()
        }
        initializePlayer()
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong("playbackPosition")
            playbackState = savedInstanceState.getInt("playbackState")
            if (playbackState == Player.STATE_READY) {
                player.seekTo(playbackPosition)
                player.play()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer() {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource =
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(false)
                .createMediaSource(MediaItem.fromUri(hlsUri))
        player = ExoPlayer.Builder(requireContext()).build()
        player.setMediaSource(hlsMediaSource)
        player.prepare()
        binding.exoplayerView.player = player
        player.addListener(this)
        player.play()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition)
        outState.putInt("playbackState", playbackState)
    }

    /*private fun updateProgressBar(totalTime: Int, channelTimestart1: Long) {
        val interval = 1000L // Интервал обновления прогресса в миллисекундах (1 секунда)

        coroutineScope.launch(Dispatchers.Main) {
            while (true) {
                val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах
                val elapsedTime =
                    currentTime - channelTimestart1 // Время, которое пользователь уже смотрит передачу

                // Вычисляем прогресс в процентах
                val progress = (elapsedTime.toFloat() / totalTime.toFloat()) * 100

                // Устанавливаем ширину полоски в процентах
                binding.progressBar.layoutParams.width =
                    (progress * resources.displayMetrics.density).toInt()
                binding.progressBar.requestLayout()

                delay(interval)
            }
        }
    }*/
    override fun onPause() {
        super.onPause()
        coroutineScope.cancel()
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

    private fun hideSystemUi() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
        activity?.let { WindowInsetsControllerCompat(it.window, binding.container) }.let {
            it?.hide(WindowInsetsCompat.Type.statusBars())
            it?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUi() {
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
        activity?.let { WindowInsetsControllerCompat(it.window, binding.container) }
            ?.show(WindowInsetsCompat.Type.systemBars())
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun showOtherViews() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.unplayedBar.visibility = View.VISIBLE
        binding.textViewTimeToTheEnd.visibility = View.VISIBLE
        binding.settings.visibility = View.VISIBLE
        binding.backToMain.visibility = View.VISIBLE
        binding.activeChannelIcon.visibility = View.VISIBLE
        binding.activeChannelDesc.visibility = View.VISIBLE
        binding.activeChannelName.visibility = View.VISIBLE
    }

    private fun hideOtherViews() {
        binding.layoutTop.visibility = View.INVISIBLE
        binding.layoutBottom.visibility = View.INVISIBLE
        binding.unplayedBar.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.textViewTimeToTheEnd.visibility = View.INVISIBLE
        binding.settings.visibility = View.INVISIBLE
        binding.backToMain.visibility = View.INVISIBLE
        binding.activeChannelIcon.visibility = View.INVISIBLE
        binding.activeChannelDesc.visibility = View.INVISIBLE
        binding.activeChannelName.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
        showSystemUi()
    }

    companion object {
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


