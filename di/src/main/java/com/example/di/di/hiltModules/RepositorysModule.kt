package com.example.di.di.hiltModules

import com.example.data.repository.ChannelRepositoryImpl
import com.example.data.repository.ChannelDownloadRepositoryFB
import com.example.data.repository.EpgRepositoryImpl
import com.example.data.repository.FavoriteChannelsRepositoryImpl
import com.example.data.repository.RadioDownloadRepositoryFB
import com.example.data.repository.RadioRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.DownloadRepository
import com.example.domain.repository.EpgRepository
import com.example.domain.repository.FavoriteChannelsRepository
import com.example.domain.repository.RadioDownloadRepository
import com.example.domain.repository.RadioRepository
import com.example.domain.repository.UserRepository
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
        downloadRepository: ChannelDownloadRepositoryFB
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

    @Binds
    @Singleton
    abstract fun bindFavoriteChannelRepository(
        favoriteChannelsRepository: FavoriteChannelsRepositoryImpl
    ): FavoriteChannelsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindRadioRepository(
        radioRepository: RadioRepositoryImpl
    ): RadioRepository

    @Binds
    @Singleton
    abstract fun bindRadioDownloadRepository(
        radioDownloadRepository: RadioDownloadRepositoryFB
    ): RadioDownloadRepository
}