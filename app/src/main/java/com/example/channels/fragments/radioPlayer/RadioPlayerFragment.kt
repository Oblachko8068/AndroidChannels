package com.example.channels.fragments.radioPlayer

import android.annotation.SuppressLint
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
import com.example.channels.databinding.FragmentRadioplayerBinding
import com.example.channels.fragments.navigator

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioplayerBinding? = null
    private val binding get() = _binding!!
    private var radioPlayerService: RadioPlayerService? = null
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startStopButton.setOnClickListener {
            togglePlayer()
        }

        binding.backToMain.setOnClickListener {
            navigator().goBack()
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RadioPlayerService.RadioBinder
                radioPlayerService = binder.service
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        val serviceIntent = Intent(requireContext(), RadioPlayerService::class.java)
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun togglePlayer() {
        radioPlayerService?.let {
            if (it.isPlaying) {
                it.pausePlayer()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                it.startPlayer()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unbindService(serviceConnection)
        _binding = null
    }
}


