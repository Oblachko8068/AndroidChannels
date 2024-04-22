package com.example.channels.musicPlayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.channels.R
import com.example.channels.databinding.FragmentMusicPlayerBinding
import com.example.domain.model.Music
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//TODO сделать кнопки рабочими
class MusicPlayerFragment(private val music: Music) : BottomSheetDialogFragment() {

    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(music.artUri)
            .into(binding.musicImage)
        binding.musicTitle.text = music.title
        binding.startStopButton.setOnClickListener {

        }

        binding.buttonNext.setOnClickListener {

        }

        binding.buttonPrevious.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}