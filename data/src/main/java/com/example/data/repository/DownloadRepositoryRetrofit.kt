package com.example.data.repository

import android.util.Log
import com.example.data.model.EpgDbEntity
import com.example.data.model.fromChannelJsonToChannelDbEntity
import com.example.data.model.fromChannelJsonToEpgDbEntity
import com.example.data.network.ChannelsApi
import com.example.data.room.ChannelDao
import com.example.data.room.EpgDao
import com.example.domain.repository.DownloadRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

class DownloadRepositoryRetrofit @Inject constructor(
    private val channelDao: ChannelDao,
    private val epgDao: EpgDao,
    private val retrofit: Retrofit
) : DownloadRepository {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ChannelViewModel", "Быстрей исправляем: ${throwable.message}")
    }

    override suspend fun fetchChannels() {
        val response = retrofit
            .create(ChannelsApi::class.java)
            .getChannelList()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val channelList = response.body()?.channels ?: emptyList()
                val channelDbEntityList =
                    channelList.map { it.fromChannelJsonToChannelDbEntity() }
                val epgDBEntityList: ArrayList<EpgDbEntity> = arrayListOf()
                channelList.forEach { epgDBEntityList.addAll(it.fromChannelJsonToEpgDbEntity()) }
                CoroutineScope(Dispatchers.IO).launch {
                    channelDao.createChannel(channelDbEntityList)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    epgDao.createEpg(epgDBEntityList)
                }
            }
        }
    }
}