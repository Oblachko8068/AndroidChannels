package com.example.channels.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.Channel
import com.example.domain.model.Epg
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository


class ChannelViewModel(
    downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
) : ViewModel() {

    private var channelLiveData: LiveData<List<Channel>> =
        channelRepository.getChannelListLiveData()
    private var epgLiveData: LiveData<List<Epg>> = epgRepository.getEpgListLiveData()

    init {
        downloadRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelLiveData
    }

    fun getEpgListLiveData(): LiveData<List<Epg>> {
        return epgLiveData
    }

}

