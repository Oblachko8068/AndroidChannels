package com.example.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.channels.repository.ChannelRepository
import com.example.channels.retrofit.ChannelJSON

class ChannelViewModel(
    private val channelRepository: ChannelRepository
) : ViewModel() {


    private val channelJSONListLiveData: LiveData<List<ChannelJSON>> =
        channelRepository.getChannelListLiveData()

    fun fetchChannels() {
        channelRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<ChannelJSON>> {
        return channelJSONListLiveData
    }
}
