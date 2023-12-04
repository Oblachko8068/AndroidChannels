package com.example.channels.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.bumptech.glide.Glide
import com.example.channels.databinding.FragmentExoplayerBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

const val CHANNEL_EXO_DATA = "CHANNEL_EXO_DATA"
const val EPG_DATA = "EPG_DATA"
const val SET_RESULT = "SET_RESULT"
const val hlsUri = "https://linear-143.frequency.stream/dist/localnow/143/hls/master/playlist.m3u8"

class ExoPlayerFragment : Fragment(), Player.Listener {

    private var visibilityView: Boolean = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var _binding: FragmentExoplayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var player: ExoPlayer
    private var playbackState: Int = Player.STATE_IDLE
    private lateinit var videoTrackGroup: Tracks.Group
    private var currentResolution = -1
    private var tracksList = mutableListOf<Int>()
    private val playerListener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            for (trackGroup in tracks.groups) {
                if (trackGroup.type == 2) {
                    videoTrackGroup = trackGroup
                    for (i in 0 until trackGroup.length) {
                        if (!tracksList.contains(trackGroup.getTrackFormat(i).width)) {
                            tracksList.add(trackGroup.getTrackFormat(i).width)
                        }
                    }
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExoplayerBinding.inflate(inflater, container, false)
        hideSystemUi()
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        val channel = arguments?.getSerializable(CHANNEL_EXO_DATA) as? Channel
        val epg = arguments?.getSerializable(EPG_DATA) as? Epg
        if (channel != null) {
            val channelName = channel.name
            val channelDescription = epg?.title
            val channelIconResource = channel.image
            val channelTimestart = epg?.timestart
            val channelTimestop = epg?.timestop
            binding.activeChannelName.text = channelName
            binding.activeChannelDesc.text = "$channelDescription"
            context?.let {
                Glide.with(it)
                    .load(channelIconResource)
                    .into(binding.activeChannelIcon)
            }
        }
        binding.backToMain.setOnClickListener {
            navigator().goBack()
        }
        binding.container.setOnClickListener {
            if (player.isPlaying) {
                coroutineScope.launch {
                    if (visibilityView) {
                        hidePlayerControls()
                        visibilityView = false
                    } else {
                        showPlayerControls()
                        visibilityView = true
                    }
                }
            }
        }

        binding.settings.setOnClickListener {
            val dialogFragment =
                QualitySettingsFragment.newInstance(tracksList, currentResolution)
            dialogFragment.show(parentFragmentManager, "setting")
        }
        if (savedInstanceState != null) {
            playbackState = savedInstanceState.getInt("playbackState")
            if (playbackState == Player.STATE_READY) {
                player.play()
            }
        }
        parentFragmentManager.setFragmentResultListener(SET_RESULT, viewLifecycleOwner) { _, res ->
            val result = res.getInt("quality")
            if (result == -1) {
                currentResolution = -1
                autoQuality()
            } else {
                currentResolution = result
                updatePlayer(tracksList.indexOf(result))
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
        val trackSelector = DefaultTrackSelector(requireContext())
        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        player.setMediaSource(hlsMediaSource)
        player.prepare()
        binding.exoplayerView.player = player
        player.play()
        player.addListener(playerListener)
    }

    private fun autoQuality() {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .clearOverride(videoTrackGroup.mediaTrackGroup)
            .build()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updatePlayer(index: Int) {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setOverrideForType(
                TrackSelectionOverride(
                    videoTrackGroup.mediaTrackGroup, index
                )
            )
            .build()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
        playbackState = player.playbackState
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onResume() {
        super.onResume()
        player.play()
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.stop()
        player.removeListener(playerListener)
        player.release()
        showSystemUi()
        _binding = null
    }

    private fun hideSystemUi() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
        activity?.let { WindowInsetsControllerCompat(it.window, binding.container) }.let {
            it?.hide(WindowInsetsCompat.Type.statusBars())
            it?.hide(WindowInsetsCompat.Type.navigationBars())
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

    private fun showPlayerControls() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
    }

    private fun hidePlayerControls() {
        binding.layoutTop.visibility = View.INVISIBLE
        binding.layoutBottom.visibility = View.INVISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("playbackState", playbackState)
    }

    companion object {
        fun newInstance(channel: Channel, selectedEpgDb: Epg?): ExoPlayerFragment {
            val args = Bundle()
            args.putSerializable(CHANNEL_EXO_DATA, channel)
            args.putSerializable(EPG_DATA, selectedEpgDb)
            val fragment = ExoPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}



