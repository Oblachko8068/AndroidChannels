package com.example.channels.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.ChannelDb
import com.example.channels.retrofit.ChannelsApi
import com.example.channels.retrofit.ChannelsJson
import com.example.channels.retrofit.EpgDb
import com.example.channels.retrofit.toChannelDb
import com.example.channels.retrofit.toEpgDb
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DownloadRepository(
    context: Context,
) {

    private var ChannellsApi: ChannelsApi
    private var channelDbLiveData = MutableLiveData<List<ChannelDb>>()
    private var epgDbLiveData = MutableLiveData<List<EpgDb>>()
    private var channelRepository: ChannelRepository = ChannelRepository(context)
    private var epgRepository: EpgRepository = EpgRepository(context)

    init {
        // Создание логгера для перехвата и отображения сетевых запросов и ответов
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Создание OkHttpClient с добавленным логгером
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Создание Retrofit-клиента для взаимодействия с API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.jsonserve.com/") // Базовый URL API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON
            .build()

        ChannellsApi = retrofit.create(ChannelsApi::class.java) // Создание API-интерфейса
    }

    fun fetchChannels() {
        ChannellsApi.getChannelList().enqueue(object : Callback<ChannelsJson> {
            override fun onFailure(call: Call<ChannelsJson>, t: Throwable) {

            }

            override fun onResponse(call: Call<ChannelsJson>, response: Response<ChannelsJson>) {
                val channelList = response.body()?.channels ?: emptyList()
                val channelDbList = channelList.map { it.toChannelDb() }
                val epgDBList: ArrayList<EpgDb> = arrayListOf()
                channelList.forEach{ epgDBList.addAll(it.toEpgDb()) }
                channelRepository.updateChannelList(channelDbList, channelDbLiveData)
                epgRepository.updateEpgList(epgDBList, epgDbLiveData)
            }
        })
    }
}
