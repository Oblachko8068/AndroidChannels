package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.channels.databinding.FragmentFavoritesBinding

class FavoritesFragment : BaseChannelFragment() {

    private var _binding: FragmentFavoritesBinding? = null
    override val binding get() = _binding!!

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        recyclerView = binding.favoriteChannelsRecyclerView
        return binding
    }
}