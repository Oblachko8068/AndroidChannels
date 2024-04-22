package com.example.channels.radioPlayer

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.channels.R
import com.example.channels.databinding.FragmentRadioplayerBinding
import com.example.channels.viewModels.RadioViewModel
import com.example.domain.model.Radio

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioplayerBinding? = null
    private val binding get() = _binding!!
    private var radioPlayerService: RadioPlayerService? = null
    private lateinit var serviceConnection: ServiceConnection
    private val radioViewModel: RadioViewModel by activityViewModels()
    private var radioList : MutableList<Radio> = mutableListOf()
    private var idRadio = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioplayerBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initServiceConnection()
        radioViewModel.getRadioLiveData().observe(viewLifecycleOwner) {
            radioList = it.toMutableList()
            if (radioList.size != 0){
                applyView()
            }
        }

        binding.buttonNext.setOnClickListener{
            idRadio = (idRadio - 1 + radioList.size) % radioList.size
            applyView()
        }

        binding.buttonPrevious.setOnClickListener {
            idRadio = (idRadio + 1) % radioList.size
            applyView()
        }

        binding.startStopButton.setOnClickListener {
            togglePlayer()
        }
    }

    private fun applyView() {
        Glide.with(this)
            .load(radioList[idRadio].image)
            .transform(RoundedCorners(20))
            .fitCenter()
            .into(binding.radioImage)
        binding.radioTitle.text = radioList[idRadio].name
        radioPlayerService?.changeTheRadio(radioList[idRadio].stream, radioList[idRadio].name)
    }

    private fun initServiceConnection() {
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
                binding.startStopButton.setImageResource(R.drawable.icon_play)
            } else {
                it.startPlayer()
                binding.startStopButton.setImageResource(R.drawable.icon_pause)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unbindService(serviceConnection)
        _binding = null
    }
}