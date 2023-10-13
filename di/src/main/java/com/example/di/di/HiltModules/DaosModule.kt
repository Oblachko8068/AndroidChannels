package com.example.di.di.HiltModules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.room.AppDatabase
import com.example.data.room.ChannelDao
import com.example.data.room.EpgDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaosModule (){

    @Provides
    @Singleton
    fun  provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(appContext, AppDatabase::class.java, "database.db").build()
    }

    @Provides
    @Singleton
    fun  provideEpgDao(appDatabase: AppDatabase): EpgDao{
        return appDatabase.getEpgDao()
    }

    @Provides
    @Singleton
    fun  provideChannelDao(appDatabase: AppDatabase): ChannelDao{
        return appDatabase.getChannelDao()
    }

}