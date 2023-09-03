package com.example.channels.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.room.ChannelDao
import com.example.channels.model.room.ChannelDbEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelRepositoryRetrofit(
    private val channelDao: ChannelDao
) : ChannelRepository {

    override fun updateChannelList(channelDbEntityList: List<ChannelDbEntity>) {
        createChannel(channelDbEntityList)
    }
    override fun createChannel(channelDbEntityList: List<ChannelDbEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            channelDao.createChannel(channelDbEntityList)
        }
    }
    override fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelDao.getChannelListAll().map { it.map{ it.toChannelDb()} }
    }
}



