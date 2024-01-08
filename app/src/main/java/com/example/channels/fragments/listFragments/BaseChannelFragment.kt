package com.example.channels.fragments.listFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.channels.RecyclerAdapter
import com.example.channels.fragments.navigator
import com.example.channels.viewModel.ChannelViewModel
import com.example.channels.viewModel.ChannelViewModel.Companion.searchTextLiveData
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseChannelFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private val channelViewModel: ChannelViewModel by activityViewModels()
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
        recyclerView?.adapter = RecyclerAdapter(
            requireContext(),
            emptyList(),
            emptyList(),
            emptyList(),
            this
        )
        val adapter = recyclerView?.adapter as? RecyclerAdapter
        val mediatorLiveData = channelViewModel.getMediatorLiveData()
        mediatorLiveData.observe(viewLifecycleOwner) {
            adapter?.setNewData(
                channelViewModel.getChannelList(this is FavoritesFragment),
                it.second
            )
        }

        val favoriteChannelLiveData = channelViewModel.getFavoriteChannelLiveData()
        favoriteChannelLiveData.observe(viewLifecycleOwner) {
            adapter?.updateFavoriteChannelList(channelViewModel.getChannelList(this is FavoritesFragment), it)
        }

        searchTextLiveData.observe(viewLifecycleOwner) {
            adapter?.filterChannels(channelViewModel.getFilteredChannels(this is FavoritesFragment))
        }
    }

    override fun onChannelItemClicked(channel: Channel, epg: Epg) {
        navigator().showVideoPlayerFragment(channel, epg)
    }

    override fun onFavoriteClicked(channel: Channel) {
        channelViewModel.favoriteChannelClicked(channel)
    }
}