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
import com.example.domain.model.Music

class MusicPlayerFragment(MusicListMA: ArrayList<Music>, currentMusic: Music) : Fragment() {

    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicPlayerService: MusicPlayerService
    private var isBound = false
    private lateinit var connection : ServiceConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initServiceConnection()
        binding.startStopButton.setOnClickListener {
            togglePlayer()
        }

        binding.buttonNext.setOnClickListener {
            if (isBound) {
                musicPlayerService.playNext()
            }
        }

        binding.buttonPrevious.setOnClickListener {
            if (isBound) {
                musicPlayerService.playPrevious()
            }
        }

        binding.buttonMusicList.setOnClickListener {

        }
    }

    private fun togglePlayer() {
        if (isBound) {
            if (musicPlayerService.isPlaying) {
                musicPlayerService.pausePlayer()
                binding.startStopButton.setImageResource(R.drawable.radio_play_button)
            } else {
                musicPlayerService.startPlayer()
                binding.startStopButton.setImageResource(R.drawable.radio_pause_button)
            }
        }
    }

    private fun initServiceConnection() {
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.MusicBinder
                musicPlayerService = binder.service
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }
        val serviceIntent = Intent(requireActivity(), MusicPlayerService::class.java)
        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
    }
}