package com.example.channels.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.Channel
import com.example.channels.retrofit.Channels
import com.example.channels.retrofit.ChannellsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChannelRepository {

    private var ChannelsApi: ChannellsApi
    private var channelListLiveData = MutableLiveData<List<Channel>>()

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.jsonserve.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ChannelsApi = retrofit.create(ChannellsApi::class.java)
    }

    fun getChannels(callback: Callback<Channels>) {
        ChannelsApi.getChannelList().enqueue(callback)
    }

    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelListLiveData
    }

    fun updateChannelList(channelList: List<Channel>) {
        channelListLiveData.value = channelList
    }
}
