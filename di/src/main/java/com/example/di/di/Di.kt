package com.example.di.di

import android.content.Context
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class Di {
    /*companion object {
       @Inject lateinit var downloadRepository: DownloadRepository
       @Inject lateinit var channelRepository: ChannelRepository
       @Inject lateinit var epgRepository: EpgRepository

       fun init() {
           Log.d("aaaaa" , "downloadRepository: $downloadRepository")
           Log.d("aaaaa" , "channelRepository: $channelRepository")
           Log.d("aaaaa" , "epgRepository: $epgRepository")
            /*val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "database.db").build()
            val channelDao = appDatabase.getChannelDao()
            val epgDao = appDatabase.getEpgDao()*/

           /* // Создание логгера для перехвата и отображения сетевых запросов и ответов
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
                .build()*/

            //channelRepository = ChannelRepositoryImpl()
            //epgRepository = EpgRepositoryImpl()
            //downloadRepository = DownloadRepositoryRetrofit()

        }
    }*/
    class EpgUseCase(
        private val epgRepository: EpgRepository
    ) {
        fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg> {
            return epgRepository.getCurrentEpgByChannelId(channelID)
        }
    }
}


