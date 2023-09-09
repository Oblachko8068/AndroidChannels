package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.example.channels.R
import com.example.channels.RecyclerAdapter
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModelFactory
import com.example.channels.fragments.navigator
import com.example.di.di.Di
import com.example.domain.model.Channel
import com.example.domain.model.Epg

abstract class BaseChannelFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    abstract var recyclerView: RecyclerView
    val recyclerViewId: Int
        get() = when (this) {
            is AllFragment -> R.id.recyclerView3
            is FavoritesFragment -> R.id.recyclerView4
            else -> throw IllegalArgumentException("")
        }
    var searchQuery: String? = null
    private lateinit var channel: List<Channel>
    private lateinit var epg: List<Epg>
    private val channelViewModel by viewModels<ChannelViewModel> {
        ChannelViewModelFactory(
            Di.downloadRepository,
            Di.channelRepository,
            Di.epgRepository
        )
    }

    abstract fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelList = channelViewModel.getChannelListLiveData()
        val epgList = channelViewModel.getEpgListLiveData()

        val mediatorLiveData = MediatorLiveData<Pair<List<Channel>, List<Epg>>>()

        mediatorLiveData.addSource(channelList) { channels ->
            val epg = epgList.value ?: emptyList()
            mediatorLiveData.value = Pair(channels, epg)
        }

        mediatorLiveData.addSource(epgList) { epg ->
            val channels = channelList.value ?: emptyList()
            mediatorLiveData.value = Pair(channels, epg)
        }

        epg = epgList.value ?: emptyList()
        channel = channelList.value ?: emptyList()
        mediatorLiveData.observe(viewLifecycleOwner, Observer { pair ->
            channel = pair.first
            epg = pair.second
            updateChannelsAndEpg()
        })

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        if (::channel.isInitialized && ::epg.isInitialized) {
            getAllChannelsList(channel, epg)
        }
    }

    protected fun createAdapter(channelList: List<Channel>, epgList: List<Epg>) {
        recyclerView.adapter = RecyclerAdapter(requireContext(), channelList, epgList, this)
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



