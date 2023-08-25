package com.example.channels.ViewModel

import androidx.lifecycle.LiveData
//import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepositoryRetrofit
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg


class ChannelViewModel(
    private val downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepositoryRetrofit,
) : ViewModel() {

    private var channelLiveData: LiveData<List<Channel>> = channelRepository.getChannelListLiveData()
    private var epgLiveData: LiveData<List<Epg>> = epgRepository.getEpgListLiveData()

    init{
        fetchChannels()
    }
    fun fetchChannels() {
        downloadRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelLiveData
    }

    fun getEpgListLiveData(): LiveData<List<Epg>>{
        return epgLiveData
    }

}

