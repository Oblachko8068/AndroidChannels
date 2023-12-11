package com.example.channels.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import com.example.domain.repository.FavoriteChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
    private var favoriteChannelsRepository: FavoriteChannelsRepository
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ChannelViewModel", "Ошибочка в загрузке пацаны: ${throwable.message}")
    }
    private var channelLiveData: LiveData<List<Channel>> =
        channelRepository.getChannelListLiveData()
    private var epgLiveData: LiveData<List<Epg>> = epgRepository.getEpgListLiveData()
    private var mediatorLiveData = MediatorLiveData<Pair<List<Channel>, List<Epg>>>()

    init {
        mediatorLiveData.addSource(channelLiveData) { channels ->
            val epg = epgLiveData.value ?: emptyList()
            mediatorLiveData.value = Pair(channels, epg)
        }
        mediatorLiveData.addSource(epgLiveData) { epg ->
            val channels = channelLiveData.value ?: emptyList()
            mediatorLiveData.value = Pair(channels, epg)
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            downloadRepository.fetchChannels()
        }
    }

    fun getFavoriteChannelRepository(): FavoriteChannelsRepository = favoriteChannelsRepository

    fun getChannelList(channels: List<Channel>, isFavoriteFragment: Boolean): List<Channel> {
        return if (isFavoriteFragment) {
            val favChannelsArray = favoriteChannelsRepository.getSavedFavChannelsArray()
            channels.filter { it.id in favChannelsArray }
        } else {
            channels
        }
    }

    fun getMediatorLiveData(): MediatorLiveData<Pair<List<Channel>, List<Epg>>> = mediatorLiveData

    fun getFilteredChannels(isFavoriteFragment: Boolean): List<Channel> {
        val channels = getChannelList(channelLiveData.value!!,isFavoriteFragment)
        val searchQuery = _searchTextLiveData.value
        return if (!searchQuery.isNullOrEmpty()) {
            channels.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channels
        }
    }

    fun favoriteChannelClicked(channel: Channel) {
        favoriteChannelsRepository.addOrRemoveChannelFromFavoriteChannels(channel)
    }

    companion object {

        private val _searchTextLiveData = MutableLiveData<String>()
        val searchTextLiveData: LiveData<String> = _searchTextLiveData

        fun setSearchText(searchText: String) {
            _searchTextLiveData.value = searchText
        }
    }
}

