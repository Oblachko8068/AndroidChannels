package com.example.channels.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import org.jetbrains.annotations.Nullable


class ChannelViewModel(
    downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
) : ViewModel() {

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
        downloadRepository.fetchChannels()
    }

    fun getMediatorLiveData(): MediatorLiveData<Pair<List<Channel>, List<Epg>>> {
        return mediatorLiveData
    }
    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelLiveData
    }

    fun getEpgListLiveData(): LiveData<List<Epg>> {
        return epgLiveData
    }

}

