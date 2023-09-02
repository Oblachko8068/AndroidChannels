package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.channels.Di
import com.example.channels.R
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentAllBinding
import com.example.channels.fragments.navigator
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg


class AllFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var channel: List<Channel>
    lateinit var epg: List<Epg>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelViewModel = Di.channelViewModel
        val channelList = channelViewModel.getChannelListLiveData()
        val epgList = channelViewModel.getEpgListLiveData()

        epg = epgList.value ?: emptyList()
        channel = channelList.value ?: emptyList()

        channelList.observe(viewLifecycleOwner, Observer { channelList ->
            channel = channelList
            updateChannelsAndEpg()
        })

        epgList.observe(viewLifecycleOwner, Observer { epgList ->
            epg = epgList
            updateChannelsAndEpg()
        })

        binding.recyclerView3.setHasFixedSize(true)
        binding.recyclerView3.layoutManager = LinearLayoutManager(requireContext())

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    updateChannelsAndEpg()
                    if (!searchQuery.isNullOrEmpty()) {
                        filterChannels(searchQuery)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun updateChannelsAndEpg() {
        if (::channel.isInitialized && ::epg.isInitialized) {
            getAllChannelsList(channel, epg)
        }
    }

    private fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>) {
        adapter = RecyclerAdapter(requireContext(), channelList, epg, this)
        binding.recyclerView3.adapter = adapter
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    fun filterChannels(searchQuery: String?) {
        val filteredList: List<Channel> = if (!searchQuery.isNullOrEmpty()) {
            channel.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channel
        }

        val adapter = binding.recyclerView3.adapter as? RecyclerAdapter
        adapter?.setData(filteredList)
    }


    override fun onChannelItemClicked(channel: Channel) {
        val epgList = epg
        val selectedEpgDb = epgList.find { it.channelID == channel.id }

        navigator().showVideoPlayerFragment(channel, selectedEpgDb)
    }
}