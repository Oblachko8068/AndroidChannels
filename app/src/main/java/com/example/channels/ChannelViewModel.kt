package com.example.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.channels.repository.ChannelRepository
import com.example.channels.repository.DownloadRepository
import com.example.channels.repository.EpgRepository
import com.example.channels.retrofit.ChannelDb
import com.example.channels.retrofit.ChannelJson
import com.example.channels.retrofit.EpgDb

class ChannelViewModel(
    private val downloadRepository: DownloadRepository,
    channelRepository: ChannelRepository,
    epgRepository: EpgRepository,
) : ViewModel() {

    private val channelDbLiveData: LiveData<List<ChannelDb>> = channelRepository.getChannelListLiveData()
    private val epgDbLiveData: LiveData<List<EpgDb>> = epgRepository.getEpgListLiveData()

    //private val channelJsonListLiveData: LiveData<List<ChannelJson>> = downloadRepository.getChannelListLiveData()

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
