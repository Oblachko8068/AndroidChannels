package com.example.channels.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.model.FavoriteChannel
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.ChannelDownloadRepository
import com.example.domain.repository.EpgRepository
import com.example.domain.repository.FavoriteChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class ChannelViewModel @Inject constructor(
    channelDownloadRepository: ChannelDownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
    private val favoriteChannelsRepository: FavoriteChannelsRepository,
    coroutineContext: CoroutineContext,
) : ViewModel() {

    private var channelLiveData: LiveData<List<Channel>> =
        channelRepository.getChannelListLiveData()
    private var epgLiveData: LiveData<List<Epg>> = epgRepository.getEpgListLiveData()
    private var mediatorLiveData = MediatorLiveData<Pair<List<Channel>, List<Epg>>>()
    private var favoriteChannelLiveData: LiveData<List<FavoriteChannel>> =
        favoriteChannelsRepository.getFavoriteChannelListLiveData()

    init {
        mediatorLiveData.addSource(channelLiveData) { channels ->
            val epg = epgLiveData.value ?: emptyList()
            mediatorLiveData.value = Pair(channels, epg)
        }
        mediatorLiveData.addSource(epgLiveData) { epg ->
            val channel = mediatorLiveData.value?.first ?: emptyList()
            mediatorLiveData.value = Pair(channel, epg)
        }
        viewModelScope.launch(coroutineContext) {
            channelDownloadRepository.fetchChannels()
        }
    }

    fun getChannelList(isFavoriteFragment: Boolean): List<Channel> {
        val channels = channelLiveData.value ?: emptyList()
        return if (isFavoriteFragment) {
            val favoriteChannels = favoriteChannelLiveData.value ?: emptyList()
            val favoriteChannelIds = favoriteChannels.map { it.channelId }
            channels.filter { channel ->
                channel.id in favoriteChannelIds
            }
        } else {
            channels
        }
    }

    fun getMediatorLiveData(): MediatorLiveData<Pair<List<Channel>, List<Epg>>> = mediatorLiveData

    fun getFilteredChannels(isFavoriteFragment: Boolean): List<Channel> {
        val channels = getChannelList(isFavoriteFragment)
        val searchQuery = _searchTextLiveData.value
        return if (!searchQuery.isNullOrEmpty()) {
            channels.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channels
        }
    }

    fun favoriteChannelClicked(channel: Channel) {
        val favoriteChannels = favoriteChannelLiveData.value ?: emptyList()
        val channelExistsInFavList = favoriteChannels.any { it.channelId == channel.id }
        if (channelExistsInFavList) {
            favoriteChannelsRepository.removeChannelFromFavoriteChannels(channel.id)
        } else {
            favoriteChannelsRepository.addChannelFromFavoriteChannels(channel.id)
        }
    }

    fun getFavoriteChannelLiveData(): LiveData<List<FavoriteChannel>> = favoriteChannelLiveData

    companion object {
        private val _searchTextLiveData = MutableLiveData<String>()
        val searchTextLiveData: LiveData<String> = _searchTextLiveData
        fun setSearchText(searchText: String) {
            _searchTextLiveData.value = searchText
        }
    }
}