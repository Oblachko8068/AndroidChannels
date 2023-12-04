package com.example.channels.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.channels.ads.AdsManager
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ChannelViewModel @Inject constructor(
    downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
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

    fun getMediatorLiveData(): MediatorLiveData<Pair<List<Channel>, List<Epg>>> {
        return mediatorLiveData
    }
}

