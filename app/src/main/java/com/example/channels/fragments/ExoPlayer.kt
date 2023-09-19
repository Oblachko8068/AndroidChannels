package com.example.channels.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.channels.databinding.CustomControllerBinding
import com.example.channels.databinding.FragmentExoplayerBinding

class ExoPlayerFragment: Fragment(), Player.Listener {
    val mp4 = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    private lateinit var mainBinding: FragmentExoplayerBinding
    private lateinit var binding: CustomControllerBinding
    private lateinit var player: ExoPlayer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        mainBinding = FragmentExoplayerBinding.inflate(inflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayer()
        addMp4Files()
        binding = CustomControllerBinding.inflate(layoutInflater)

        if (savedInstanceState != null) {
            savedInstanceState.getInt("MediaItem").let {restoredMedia ->
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMedia, seekTime)
                player.play()
            }
        }
        val back = binding.backToMain
        back.setOnClickListener {
            navigator().goBack()
        }
    }
    private fun setupPlayer(){
        player = ExoPlayer.Builder(requireContext()).build()
        mainBinding.exoplayerView.player = player
        player.addListener(this)
    }
    private fun addMp4Files(){
        player.addMediaItem(MediaItem.fromUri(mp4))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime", player.currentPosition)
        outState.putInt("MediaItem", player.currentMediaItemIndex)
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
        player.release()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}


