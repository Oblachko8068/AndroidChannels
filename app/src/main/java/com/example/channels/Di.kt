package com.example.channels

import android.content.Context
import androidx.room.Room
import com.example.channels.model.repository.*
import com.example.channels.model.room.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Di {
    companion object {
        lateinit var downloadRepository: DownloadRepository
        lateinit var channelRepository: ChannelRepository
        lateinit var epgRepository: EpgRepository

        fun init(context: Context) {
            val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "database.db").build()
            val channelDao = appDatabase.getChannelDao()
            val epgDao = appDatabase.getEpgDao()

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

            val retrofit2 = Retrofit.Builder()
                .baseUrl("https://api.jsonserve.com/") // Базовый URL API
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON
                .build()

            channelRepository = ChannelRepository(context, channelDao)
            epgRepository = EpgRepository(context, epgDao)
            downloadRepository = DownloadRepositoryOkhttp(context, channelRepository, epgRepository, retrofit)

        }
    }
}