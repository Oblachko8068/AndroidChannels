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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val channel_data = "channel_exo_data"
const val epg_data = "epg_exo_data"
const val set_Result = "setResult"
const val hlsUri = "https://linear-143.frequency.stream/dist/localnow/143/hls/master/playlist.m3u8"

class ExoPlayerFragment : Fragment(), Player.Listener {

    private var visibilityView: Boolean = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: FragmentExoplayerBinding
    private lateinit var player: ExoPlayer
    private var playbackState: Int = Player.STATE_IDLE
    private lateinit var videoTrackGroup: Tracks.Group
    private var currentResolution = -1
    private var tracksList = mutableListOf<Int>()

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
        val channel = arguments?.getSerializable(channel_data) as? Channel
        val epg = arguments?.getSerializable(epg_data) as? Epg
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
        parentFragmentManager.setFragmentResultListener(set_Result, viewLifecycleOwner) { _, res ->
            val result = res.getInt("quality")
            if (result == -1) {
                currentResolution = -1
                autoQuality()
            } else {
                currentResolution = result
                updatePlayer(tracksList.indexOf(result))
            }
        }
        initializePlayer()
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
                    videoTrackGroup.mediaTrackGroup, index))
            .build()
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
        player.addListener(
            object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    for (trackGroup in tracks.groups) {
                        if (trackGroup.type == 2){
                            videoTrackGroup = trackGroup
                            for (i in 0 until trackGroup.length) {
                                if (!tracksList.contains(trackGroup.getTrackFormat(i).width)){
                                    tracksList.add(trackGroup.getTrackFormat(i).width)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("playbackState", playbackState)
    }

    override fun onPause() {
        super.onPause()
        playbackState = player.playbackState
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.play()
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

    private fun showOtherViews() {
        binding.layoutTop.visibility = View.VISIBLE
        binding.layoutBottom.visibility = View.VISIBLE
    }

    private fun hideOtherViews() {
        binding.layoutTop.visibility = View.INVISIBLE
        binding.layoutBottom.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
        player.stop()
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


