package com.example.di.di.HiltModules

import com.example.data.repository.ChannelRepositoryImpl
import com.example.data.repository.DownloadRepositoryRetrofit
import com.example.data.repository.EpgRepositoryImpl
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositorysModule {

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepository: DownloadRepositoryRetrofit
    ): DownloadRepository

    @Binds
    @Singleton
    abstract fun bindChannelRepository(
        channelRepository: ChannelRepositoryImpl
    ): ChannelRepository

    @Binds
    @Singleton
    abstract fun bindEpgRepository(
        epgRepository: EpgRepositoryImpl
    ): EpgRepository

}