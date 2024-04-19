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
import com.example.channels.R
import com.example.channels.databinding.FragmentRadioplayerBinding
import com.example.channels.fragments.navigator
import com.example.channels.viewModels.RadioViewModel
import com.example.domain.model.Radio

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioplayerBinding? = null
    private val binding get() = _binding!!
    private var radioPlayerService: RadioPlayerService? = null
    private lateinit var serviceConnection: ServiceConnection
    private  val radioViewModel: RadioViewModel by activityViewModels()
    //lateinit var radioList : List<Radio>
    var idRadio = 0
    var sizeRadio = 0
    var listImege: MutableList<String> = mutableListOf()
    var listName: MutableList<String> = mutableListOf()

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


        radioViewModel.getRadioLiveData().observe(viewLifecycleOwner) { it->
            it.forEach{
                listImege.add(it.image)
                listName.add(it.name)
            }
            sizeRadio = it.size
            initializationImageAndTextViews()
        }

        binding.buttonNext.setOnClickListener{
            if (idRadio == 0){
                idRadio = sizeRadio - 1
                initializationImageAndTextViews()
            }
            else{
                idRadio -= 1
                initializationImageAndTextViews()
            }
        }

        binding.buttonPrevious.setOnClickListener {
            if (idRadio == sizeRadio - 1){
                idRadio = 0
                initializationImageAndTextViews()
            }
            else{
                idRadio += 1
                initializationImageAndTextViews()
            }
        }

        binding.startStopButton.setOnClickListener {
            togglePlayer()
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
    private fun initializationImageAndTextViews() {

            val imageUrl = listImege[idRadio]
            Glide.with(this)
               .load(imageUrl)
                .into(binding.radioImage)

           binding.radioTitle.setText(listName[idRadio])



    }

    private fun togglePlayer() {
        radioPlayerService?.let {
            if (it.isPlaying) {
                it.pausePlayer()
                binding.startStopButton.setImageResource(R.drawable.radio_play_button)
            } else {
                it.startPlayer()
                binding.startStopButton.setImageResource(R.drawable.radio_pause_button)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unbindService(serviceConnection)
        _binding = null
    }
}


