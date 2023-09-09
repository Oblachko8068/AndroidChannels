package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentAllBinding
import com.example.domain.model.Channel
import com.example.domain.model.Epg


class AllFragment : BaseChannelFragment() {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!
    override var recyclerView: RecyclerView = binding.recyclerView3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding {
        return FragmentAllBinding.inflate(inflater, container, false)
    }

    override fun onPageChanged(position: Int) {
        if (position == 0) {
            updateChannelsAndEpg()
            if (!searchQuery.isNullOrEmpty()) {
                filterChannels(searchQuery)
            }
        }
    }

    override fun filterChannels(searchQuery: String?) {
        val filteredList = filterChannelsCommon(searchQuery)
        val adapter = recyclerView?.adapter as? RecyclerAdapter
        adapter?.setData(filteredList)
    }

    override fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>) {
        createAdapter(channelList, epg)
    }
}