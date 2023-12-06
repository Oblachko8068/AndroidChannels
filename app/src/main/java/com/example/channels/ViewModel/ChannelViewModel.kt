package com.example.channels.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import com.example.domain.repository.FavoriteChannelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ChannelViewModel", "Error fetching channels: ${throwable.message}")
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
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            downloadRepository.fetchChannels()
        }
    }
    fun getFavoriteChannelRepository(): FavoriteChannelsRepository = favoriteChannelsRepository
    fun getChannelList(isFavoriteFragment: Boolean): List<Channel> {
        return if (isFavoriteFragment) {
            val favChannelsArray = favoriteChannelsRepository.getSavedFavChannelsArray()
            val channels = channelLiveData.value ?: emptyList()
            channels.filter { it.id in favChannelsArray }
        } else {
            channelLiveData.value ?: emptyList()
        }
    }

    fun getEpgList(): List<Epg> = epgLiveData.value ?: emptyList()
    fun getMediatorLiveData(): MediatorLiveData<Pair<List<Channel>, List<Epg>>> = mediatorLiveData
    fun getFilteredChannels(searchQuery: String?, isFavoriteFragment: Boolean): List<Channel> {
        val channels = getChannelList(isFavoriteFragment)

        return if (!searchQuery.isNullOrEmpty()) {
            channels.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channels
        }
    }
}

