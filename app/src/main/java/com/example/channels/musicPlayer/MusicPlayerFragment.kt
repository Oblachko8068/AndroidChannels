package com.example.channels.musicPlayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentMusicPlayerBinding


class MusicPlayerFragment : Fragment() {

    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicPlayerService: MusicPlayerService
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicPlayerService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Connect to the MusicPlayerService
        val serviceIntent = Intent(requireContext(), MusicPlayerService::class.java)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        // Setup UI and playback controls
        // For example:
        binding.startStopButton.setOnClickListener {
            if (isBound) {
                if (musicPlayerService.isPlaying) {
                    musicPlayerService.pauseMusic()
                    binding.startStopButton.setImageResource(R.drawable.radio_play_button)
                } else {
                    musicPlayerService.playMusic()
                    binding.startStopButton.setImageResource(R.drawable.radio_pause_button)
                }
            }
        }

        binding.buttonNext.setOnClickListener {
            // Implement logic for skipping to next track
            if (isBound) {
                musicPlayerService.playNext()
                binding.buttonNext.setImageResource(R.drawable.radio_next_button)
            }
        }

        binding.buttonPrevious.setOnClickListener {
            // Implement logic for skipping to previous track
            if (isBound) {
                musicPlayerService.playPrevious()
                binding.buttonPrevious.setImageResource(R.drawable.radio_previous_button)
            }
        }

        binding.buttonRadioList.setOnClickListener {
            // Implement logic for showing list of radio stations
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Unbind from the service
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
    }
}