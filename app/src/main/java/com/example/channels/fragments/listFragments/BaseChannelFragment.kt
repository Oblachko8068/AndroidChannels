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

        mediatorLiveData.observe(viewLifecycleOwner) {
            createAdapter()
        }

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
                createAdapter()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    protected fun createAdapter() {
        val channelList = channelViewModel.getChannelList(this is FavoritesFragment)
        val epgList = channelViewModel.getEpgList()
        recyclerView?.adapter = RecyclerAdapter(requireContext(), channelList, epgList, this, channelViewModel.getFavoriteChannelRepository())
        if (!searchQuery.isNullOrEmpty()) {
            filterChannelsBySearch(searchQuery)
        }
    }

    fun filterChannelsBySearch(searchQuery: String?) {
        val filteredList = channelViewModel.getFilteredChannels(searchQuery, this is FavoritesFragment)
        val adapter = recyclerView?.adapter as? RecyclerAdapter
        adapter?.setData(filteredList)
    }

    override fun onChannelItemClicked(channel: Channel, epg: Epg) {
        navigator().showVideoPlayerFragment(channel, epg)
    }
}
