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
import com.example.channels.RecyclerAdapter
import com.example.channels.ViewModel.ChannelViewModel
import com.example.channels.ViewModel.ChannelViewModel.Companion.searchTextLiveData
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
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val mediatorLiveData = channelViewModel.getMediatorLiveData()

        mediatorLiveData.observe(viewLifecycleOwner) {
            createAdapter(it.first, it.second)
        }

        searchTextLiveData.observe(viewLifecycleOwner){
            val adapter = recyclerView?.adapter as? RecyclerAdapter
            adapter?.setData(channelViewModel.getFilteredChannels(this is FavoritesFragment))
        }
    }

    private fun createAdapter(channelList: List<Channel>, epgList: List<Epg>) {
        recyclerView?.adapter = RecyclerAdapter(
            requireContext(),
            channelViewModel.getChannelList(channelList, this is FavoritesFragment),
            epgList,
            this,
            channelViewModel.getFavoriteChannelRepository()
        )
    }

    override fun onChannelItemClicked(channel: Channel, epg: Epg) {
        navigator().showVideoPlayerFragment(channel, epg)
    }

    override fun onFavoriteClicked(channel: Channel) {
        channelViewModel.favoriteChannelClicked(channel)
    }
}
