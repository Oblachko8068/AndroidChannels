package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.domain.model.Channel
import com.example.data.model.ChannelDbEntity
import com.example.data.room.ChannelDao
import com.example.domain.repository.ChannelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao
) : ChannelRepository {

    override fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelDao.getChannelListAll().map { channelDbEntityList -> channelDbEntityList.map{ it.toChannelDb()} }
    }
}



