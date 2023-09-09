package com.example.di.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.domain.model.Epg
import com.example.data.repository.ChannelRepositoryImpl
import com.example.data.repository.DownloadRepositoryRetrofit
import com.example.data.repository.EpgRepositoryImpl
import com.example.data.room.AppDatabase
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Di {
    companion object {
        lateinit var downloadRepository: DownloadRepository
        lateinit var channelRepository: ChannelRepository
        lateinit var epgRepository: EpgRepository

        fun init(context: Context, activity: AppCompatActivity) {
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

            channelRepository = ChannelRepositoryImpl(channelDao)
            epgRepository = EpgRepositoryImpl(epgDao)
            downloadRepository = DownloadRepositoryRetrofit(channelDao, epgDao, retrofit)

        }
    }
    class EpgUseCase(
        private val epgRepository: com.example.domain.repository.EpgRepository
    ) {
        fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg> {
            return epgRepository.getCurrentEpgByChannelId(channelID)
        }
    }
}


