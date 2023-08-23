package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import com.example.channels.model.retrofit.ChannelDb

interface ChannelRepository {

    fun getChannelListLiveData() : LiveData<List<ChannelDb>>

    fun updateChannelList(channelDbList: List<ChannelDb>)

    fun createChannel(channelDb: List<ChannelDb>)
}