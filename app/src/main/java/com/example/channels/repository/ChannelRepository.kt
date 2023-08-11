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

        ChannelsApi = retrofit.create(ChannellsApi::class.java) // Создание API-интерфейса
    }

    fun fetchChannels() {
        ChannelsApi.getChannelList().enqueue(object : Callback<Channels> {
            override fun onFailure(call: Call<Channels>, t: Throwable) {
                // Обработка ошибки, если не удалось получить данные
            }

            override fun onResponse(call: Call<Channels>, response: Response<Channels>) {
                val channelList = response.body()?.channels ?: emptyList()
                updateChannelList(channelList)
            }
        })
    }

    // Запрос к серверу для получения списка каналов
    fun getChannels(callback: Callback<Channels>) {
        ChannelsApi.getChannelList().enqueue(callback)
    }

    // Возвращает LiveData, которая будет содержать список каналов
    fun getChannelListLiveData(): LiveData<List<Channel>> {
        return channelListLiveData
    }

    // Обновляет LiveData с новым списком каналов
    fun updateChannelList(channelList: List<Channel>) {
        channelListLiveData.value = channelList
    }
}

