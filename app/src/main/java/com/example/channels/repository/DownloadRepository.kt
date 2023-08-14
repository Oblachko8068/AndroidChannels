package com.example.channels.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.channels.retrofit.ChannelDB
import com.example.channels.retrofit.ChannelJSON
import com.example.channels.retrofit.ChannelsApi
import com.example.channels.retrofit.ChannelsJSON
import com.example.channels.retrofit.toChannelDB
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DownloadRepository(
    private val context: Context
) {

    private var ChannellsApi: ChannelsApi
    private var channelJSONListLiveData = MutableLiveData<List<ChannelJSON>>()

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
        ChannellsApi.getChannelList().enqueue(object : Callback<ChannelsJSON> {
            override fun onFailure(call: Call<ChannelsJSON>, t: Throwable) {

            }

            override fun onResponse(call: Call<ChannelsJSON>, response: Response<ChannelsJSON>) {
                val channelList = response.body()?.channels ?: emptyList()
                val channelDBList = channelList.map { it.toChannelDB() }
                //val epgDBList: ArrayList<EpgDB> = arrayListOf()
                //channelList.forEach{ epgDBList.addAll(it.toEpgDB()) }
                updateChannelList(channelList)
            }
        })
    }

    // Запрос к серверу для получения списка каналов
    fun getChannels(callback: Callback<ChannelsJSON>) {
        ChannellsApi.getChannelList().enqueue(callback)
    }

    // Возвращает LiveData, которая будет содержать список каналов
    fun getChannelListLiveData(): LiveData<List<ChannelJSON>> {
        return channelJSONListLiveData
    }

    // Обновляет LiveData с новым списком каналов
    fun updateChannelList(channelJSONList: List<ChannelJSON>) {
        channelJSONListLiveData.value = channelJSONList
    }

    fun saveToSharedPref(channelDb: List<ChannelDB>) {
        val sharedPref = context.getSharedPreferences(
            "SharedPref",
            Context.MODE_PRIVATE
        )
        //sharedPref.edit().

    }
}

