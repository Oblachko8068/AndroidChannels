package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.example.channels.Di
import com.example.channels.R
import com.example.channels.RecyclerAdapter
import com.example.channels.fragments.navigator
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg

abstract class BaseChannelFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: ViewBinding? = null
    open val binding get() = _binding!!
    val recyclerViewId: Int
        get() = when (this) {
            is AllFragment -> R.id.recyclerView3
            is FavoritesFragment -> R.id.recyclerView4
            else -> throw IllegalArgumentException("")
        }
    protected var recyclerView: RecyclerView? = null

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var channel: List<Channel>
    lateinit var epg: List<Epg>

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

        val channelViewModel = Di.channelViewModel
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

        recyclerView = view.findViewById(recyclerViewId)
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
        if (::channel.isInitialized && ::epg.isInitialized) {
            getAllChannelsList(channel, epg)
        }
    }

    protected fun createAdapter(channelList: List<Channel>, epgList: List<Epg>) {
        adapter = RecyclerAdapter(requireContext(), channelList, epgList, this)
        recyclerView?.adapter = adapter
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



