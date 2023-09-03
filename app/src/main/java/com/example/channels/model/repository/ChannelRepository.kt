package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.room.ChannelDbEntity

interface ChannelRepository {

    fun getChannelListLiveData() : LiveData<List<Channel>>

    fun updateChannelList(channelDbList: List<ChannelDbEntity>)

    fun createChannel(channelDb: List<ChannelDbEntity>)
}