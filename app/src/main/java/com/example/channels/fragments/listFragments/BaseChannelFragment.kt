package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.example.channels.R
import com.example.channels.RecyclerAdapter
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.fragments.navigator
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseChannelFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private val channelViewModel: ChannelViewModel by viewModels()

    private var _binding: ViewBinding? = null
    open val binding get() = _binding!!
    protected var recyclerView: RecyclerView? = null

    var searchQuery: String? = null
    private var channel: List<Channel> = emptyList()
    private var epg: List<Epg> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = onCreateViewBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    abstract fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediatorLiveData = channelViewModel.getMediatorLiveData()

        mediatorLiveData.observe(viewLifecycleOwner) { pair ->
            channel = pair.first
            epg = pair.second
            updateChannelsAndEpg()
        }
        updateChannelsAndEpg()
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onPageChanged(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    abstract fun onPageChanged(position: Int)

    fun updateChannelsAndEpg() {
        getAllChannelsList(channel, epg)
    }

    protected fun createAdapter(channelList: List<Channel>, epgList: List<Epg>) {
        recyclerView?.adapter = RecyclerAdapter(requireContext(), channelList, epgList, this)
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    abstract fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>)

    protected fun filterChannelsCommon(searchQuery: String?): List<Channel> {
        return if (!searchQuery.isNullOrEmpty()) {
            channel.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channel
        }
    }

    abstract fun filterChannels(searchQuery: String?)

    override fun onChannelItemClicked(channel: Channel, epg: Epg) {
        navigator().showVideoPlayerFragment(channel, epg)
    }
}
