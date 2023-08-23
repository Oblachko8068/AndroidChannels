package com.example.channels.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.channels.model.retrofit.ChannelDb
import com.example.channels.model.room.ChannelDao
import com.example.channels.model.room.fromChannelDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelRepositoryRetrofit(
    context: Context,
    private val channelDao: ChannelDao
) : ChannelRepository {

    override fun updateChannelList(channelDbList: List<ChannelDb>) {
        createChannel(channelDbList)
        /*for (channel in channelDbList) {
            createChannel(channel)
        }*/
    }
    override fun createChannel(channelDb: List<ChannelDb>) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = channelDb.map{ it.fromChannelDb() }
            channelDao.createChannel(entity)
        }
    }
    override fun getChannelListLiveData(): LiveData<List<ChannelDb>> {
        return channelDao.getChannelListAll()
    }
}



