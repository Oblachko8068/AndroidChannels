package com.example.channels.model.repository

import android.content.Context
import com.example.channels.model.retrofit.ChannelsApi
import com.example.channels.model.retrofit.ChannelsJson
import com.example.channels.model.retrofit.EpgDb
import com.example.channels.model.retrofit.toChannelDb
import com.example.channels.model.retrofit.toEpgDb
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DownloadRepositoryOkhttp(
    private val context: Context,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository,
): DownloadRepository {

    override fun fetchChannels() {
        OkHttpClient()
            .create(ChannelsApi::class.java)
            .getChannelList().enqueue(object : Callback<ChannelsJson> {
            override fun onFailure(call: Call<ChannelsJson>, t: Throwable) {}

            override fun onResponse(call: Call<ChannelsJson>, response: Response<ChannelsJson>) {
                val channelList = response.body()?.channels ?: emptyList()
                val channelDbList = channelList.map { it.toChannelDb() }
                val epgDBList: ArrayList<EpgDb> = arrayListOf()
                channelList.forEach{ epgDBList.addAll(it.toEpgDb()) }
                channelRepository.updateChannelList(channelDbList)
                epgRepository.updateEpgList(epgDBList)
            }
        })
    }

    override fun fetchEpg() {}
}
