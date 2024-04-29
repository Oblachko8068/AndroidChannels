package com.example.channels.exoPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import androidx.media3.ui.AspectRatioFrameLayout
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentExoplayerBinding
import com.example.channels.exoPlayer.PipManager.enterPipMode
import com.example.channels.exoPlayer.PipManager.setPipPauseParams
import com.example.channels.exoPlayer.PipManager.setPipPlayParams
import com.example.channels.navigator
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

const val CHANNEL_EXO_DATA = "CHANNEL_EXO_DATA"
const val EPG_DATA = "EPG_DATA"
const val SET_RESULT = "SET_RESULT"
const val trackType = 2
const val autoQualityId = -1

class ExoPlayerFragment : Fragment(), Player.Listener, PiPModeActionsListener {

    private var _binding: FragmentExoplayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var player: ExoPlayer
    private var playbackState: Int = Player.STATE_IDLE
    private lateinit var videoTrackGroup: Tracks.Group
    private var currentResolution = autoQualityId
    private var tracksList = mutableListOf<Int>()
    private val playerListener = initListener()
    private var visibilityView: Boolean = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var wakeLock: WakeLock

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExoplayerBinding.inflate(inflater, container, false)
        hideSystemUi()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "ExoPlayerFragment:WakeLock")
        if (savedInstanceState != null) {
            playbackState = savedInstanceState.getInt("playbackState")
            if (playbackState == Player.STATE_READY) {
                player.play()
            }
        }
        binding.volume.setOnClickListener {
            if (player.volume > 0f) {
                player.volume = 0f
                binding.volume.setImageResource(R.drawable.icon_volume_on)
            } else {
                player.volume = 1f
                binding.volume.setImageResource(R.drawable.icon_volume_off)
            }
        }
        binding.backToMain.setOnClickListener {
            navigator().goBack()
        }
        binding.pipmode.setOnClickListener {
            enterPipMode(activity, this)
        }
        binding.zoom.setOnClickListener {
            if (binding.exoplayerView.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM){
                binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                binding.zoom.setImageResource(R.drawable.icon_zoom_player)
            } else {
                binding.exoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                binding.zoom.setImageResource(R.drawable.icon_fit_player)
            }
        }
        binding.container.setOnClickListener {
            if (player.isPlaying) {
                coroutineScope.launch {
                    visibilityView = if (visibilityView) hidePlayerControls().let { false }
                    else showPlayerControls().let { true }
                }
            }
        }
        binding.settings.setOnClickListener {
            val dialogFragment =
                QualitySettingsFragment.newInstance(tracksList, currentResolution)
            dialogFragment.show(parentFragmentManager, "setting")
        }
        parentFragmentManager.setFragmentResultListener(SET_RESULT, viewLifecycleOwner) { _, res ->
            val result = res.getInt("quality")
            if (result == autoQualityId) {
                currentResolution = autoQualityId
                autoQuality()
            } else {
                currentResolution = result
                updatePlayerQuality(tracksList.indexOf(result))
            }
        }
    }

    private fun initLayout() {
        val channel = arguments?.getSerializable(CHANNEL_EXO_DATA) as? Channel
        val epg = arguments?.getSerializable(EPG_DATA) as? Epg
        if (channel != null) {
            initializePlayer(channel.stream)
            val channelName = channel.name
            val channelDescription = epg?.title
            val channelIconResource = channel.image
            binding.activeChannelName.text = channelName
            binding.activeChannelDesc.text = "$channelDescription"
            context?.let {
                Glide.with(it)
                    .load(channelIconResource)
                    .into(binding.activeChannelIcon)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer(stream: String) {
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val hlsMediaSource =
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(false)
                .createMediaSource(MediaItem.fromUri(stream))
        val trackSelector = DefaultTrackSelector(requireContext())
        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        player.apply {
            setMediaSource(hlsMediaSource)
            addListener(playerListener)
            prepare()
        }
        binding.exoplayerView.player = player
        player.play()
    }

    private fun autoQuality() {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .clearOverride(videoTrackGroup.mediaTrackGroup)
            .build()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updatePlayerQuality(index: Int) {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setOverrideForType(TrackSelectionOverride(videoTrackGroup.mediaTrackGroup, index))
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPlayClick() {
        player.play()
        setPipPauseParams()
        if (!wakeLock.isHeld){
            wakeLock.acquire(10*60*1000L)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPauseClick() {
        player.pause()
        setPipPlayParams()
        if (wakeLock.isHeld){
            wakeLock.release()
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            hidePlayerControls()
        } else {
            showPlayerControls()
        }
    }

    override fun onStop() {
        super.onStop()
        if (activity?.isInPictureInPictureMode == true) {
            player.pause()
        }
    }

    override fun onPause() {
        super.onPause()
        playbackState = player.playbackState
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearPlayer()
        showSystemUi()
        coroutineScope.cancel()
        _binding = null
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    private fun clearPlayer() {
        player.apply {
            stop()
            removeListener(playerListener)
            clearMediaItems()
            clearVideoSurface()
            release()
        }
    }

    private fun initListener(): Player.Listener = object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            for (trackGroup in tracks.groups) {
                if (trackGroup.type == trackType) {
                    videoTrackGroup = trackGroup
                    for (i in 0 until trackGroup.length) {
                        trackGroup.getTrackFormat(i).width.takeUnless { it in tracksList }
                            ?.let { tracksList.add(it) }
                    }
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    showLoadingIndicator()
                    showPlayerControls()
                }
                Player.STATE_READY, Player.STATE_ENDED, Player.STATE_IDLE -> hideLoadingIndicator()
            }
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            if (isLoading) {
                showLoadingIndicator()
                showPlayerControls()
            } else hideLoadingIndicator()
        }
    }

    private fun hideLoadingIndicator() {
        binding.loadingBar.visibility = View.GONE
    }

    private fun showLoadingIndicator() {
        binding.loadingBar.visibility = View.VISIBLE
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