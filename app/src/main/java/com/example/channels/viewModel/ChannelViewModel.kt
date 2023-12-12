package com.example.channels.viewModel

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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class ChannelItem(
    val channel: Channel,
    var isFavorite: Boolean,
)

@HiltViewModel
class ChannelViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
    private var favoriteChannelsRepository: FavoriteChannelsRepository,
    coroutineContext: CoroutineContext,
) : ViewModel() {

    private var channelLiveData: LiveData<List<Channel>> =
        channelRepository.getChannelListLiveData()
    private var epgLiveData: LiveData<List<Epg>> = epgRepository.getEpgListLiveData()
    private var mediatorLiveData = MediatorLiveData<Pair<List<ChannelItem>, List<Epg>>>()

    init {
        mediatorLiveData.addSource(channelLiveData) { channels ->
            val epg = epgLiveData.value ?: emptyList()
            val favChannels = favoriteChannelsRepository.getSavedFavChannelsArray()
            val channelItems = channels.map { ChannelItem(it, it.id in favChannels) }
            mediatorLiveData.value = Pair(channelItems, epg)
        }
        mediatorLiveData.addSource(epgLiveData) { epg ->
            val channelItems = mediatorLiveData.value?.first ?: emptyList()
            mediatorLiveData.value = Pair(channelItems, epg)
        }
        viewModelScope.launch(coroutineContext) {
            downloadRepository.fetchChannels()
        }
    }

    fun getChannelList(isFavoriteFragment: Boolean): List<ChannelItem> {
        val channels = mediatorLiveData.value?.first ?: emptyList()
        return if (isFavoriteFragment) {
            channels.filter { it.isFavorite }
        } else {
            channels
        }
    }

    fun getMediatorLiveData(): MediatorLiveData<Pair<List<ChannelItem>, List<Epg>>> = mediatorLiveData

    fun getFilteredChannels(isFavoriteFragment: Boolean): List<ChannelItem> {
        val channels = getChannelList(isFavoriteFragment)
        val searchQuery = _searchTextLiveData.value
        return if (!searchQuery.isNullOrEmpty()) {
            channels.filter {
                it.channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channels
        }
    }

    fun favoriteChannelClicked(channel: ChannelItem) {
        favoriteChannelsRepository.addOrRemoveChannelFromFavoriteChannels(channel.channel)
        channel.isFavorite = !channel.isFavorite
        val updatedList = mediatorLiveData.value?.first?.toMutableList() ?: mutableListOf()
        val index = updatedList.indexOfFirst { it.channel.id == channel.channel.id }
        if (index != -1) {
            updatedList[index] = channel
            mediatorLiveData.value = Pair(updatedList, mediatorLiveData.value?.second ?: emptyList())
        }
    }

    companion object {
        private val _searchTextLiveData = MutableLiveData<String>()
        val searchTextLiveData: LiveData<String> = _searchTextLiveData

        fun setSearchText(searchText: String) {
            _searchTextLiveData.value = searchText
        }
    }
}