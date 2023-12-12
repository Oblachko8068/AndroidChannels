package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentAllBinding
import com.example.domain.model.Channel

class AllFragment : BaseChannelFragment() {

    private var _binding: FragmentAllBinding? = null
    override val binding get() = _binding!!

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        recyclerView = binding.allChannelsRecyclerView
        return binding
    }
}