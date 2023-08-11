package com.example.channels

import retrofit2.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.channels.retrofit.Channel
import com.example.channels.repository.ChannelRepository
import com.example.channels.retrofit.Channels
import retrofit2.Response
import retrofit2.Callback

class ChannelViewModel : ViewModel() {

    private val channelRepository = ChannelRepository()
    private val channelListLiveData: LiveData<List<Channel>> = channelRepository.getChannelListLiveData()

    fun fetchChannels() {
        channelRepository.fetchChannels()
    }

    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelListLiveData
    }
}
