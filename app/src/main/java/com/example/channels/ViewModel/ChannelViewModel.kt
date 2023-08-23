package com.example.channels.ViewModel

import androidx.lifecycle.LiveData
//import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.channels.model.repository.ChannelRepository
import com.example.channels.model.repository.DownloadRepository
import com.example.channels.model.repository.EpgRepositoryRetrofit
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.retrofit.EpgDb


class ChannelViewModel(
    private val downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepositoryRetrofit,
) : ViewModel() {

    private var channelDbLiveData: LiveData<List<ChannelDb>> = channelRepository.getChannelListLiveData()
    private var epgDbLiveData: LiveData<List<EpgDb>> = epgRepository.getEpgListLiveData()

    fun fetchChannels() {
        downloadRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
        return channelDbLiveData
    }

    fun getEpgListLiveData(): LiveData<List<EpgDb>>{
        return epgDbLiveData
    }

}

