package com.example.channels.model.repository

import android.content.Context
import com.example.channels.model.retrofit.ChannelsApi
import com.example.channels.model.retrofit.ChannelsJson
import com.example.channels.model.room.EpgDbEntity
import com.example.channels.model.room.fromChannelJsonToChannelDbEntity
import com.example.channels.model.room.fromChannelJsonToEpgDbEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DownloadRepositoryRetrofit(
    private val context: Context,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository,
    private val retrofit: Retrofit
) : DownloadRepository {

    override fun fetchChannels() {
        retrofit
            .create(ChannelsApi::class.java)
            .getChannelList().enqueue(object : Callback<ChannelsJson> {
                override fun onFailure(call: Call<ChannelsJson>, t: Throwable) {}

                override fun onResponse(call: Call<ChannelsJson>, response: Response<ChannelsJson>) {
                    val channelList = response.body()?.channels ?: emptyList()
                    val channelDbEntityList = channelList.map { it.fromChannelJsonToChannelDbEntity() }
                    val epgDBEntityList: ArrayList<EpgDbEntity> = arrayListOf()
                    channelList.forEach{ epgDBEntityList.addAll(it.fromChannelJsonToEpgDbEntity()) }
                    channelRepository.updateChannelList(channelDbEntityList)
                    epgRepository.updateEpgList(epgDBEntityList)
                }
            })
    }
}