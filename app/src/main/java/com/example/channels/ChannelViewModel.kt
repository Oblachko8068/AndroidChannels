package com.example.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.channels.repository.DownloadRepository
import com.example.channels.retrofit.ChannelJSON

class ChannelViewModel(
    private val downloadRepository: DownloadRepository
) : ViewModel() {


    private val channelJSONListLiveData: LiveData<List<ChannelJSON>> =
        downloadRepository.getChannelListLiveData()

    fun fetchChannels() {
        downloadRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<ChannelJSON>> {
        return channelJSONListLiveData
    }
}
