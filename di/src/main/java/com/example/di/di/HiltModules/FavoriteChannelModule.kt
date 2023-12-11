package com.example.di.di.HiltModules

import android.app.Application
import android.content.Context
import com.example.data.repository.SharedPrefRepositoryImpl
import com.example.domain.repository.SharedPrefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteChannelsModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}