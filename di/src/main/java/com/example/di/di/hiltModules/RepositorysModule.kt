package com.example.di.di.hiltModules

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.ChannelRepositoryImpl
import com.example.data.repository.ChannelDownloadRepositoryImpl
import com.example.data.repository.EpgRepositoryImpl
import com.example.data.repository.FavoriteChannelsRepositoryImpl
import com.example.data.repository.FbDatabaseRepositoryImpl
import com.example.data.repository.FbStorageRepositoryImpl
import com.example.data.repository.RadioDownloadRepositoryImpl
import com.example.data.repository.RadioRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.ChannelRepository
import com.example.domain.repository.ChannelDownloadRepository
import com.example.domain.repository.EpgRepository
import com.example.domain.repository.FavoriteChannelsRepository
import com.example.domain.repository.FbDatabaseRepository
import com.example.domain.repository.FbStorageRepository
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
        downloadRepository: ChannelDownloadRepositoryImpl
    ): ChannelDownloadRepository

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
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRadioRepository(
        radioRepository: RadioRepositoryImpl
    ): RadioRepository

    @Binds
    @Singleton
    abstract fun bindRadioDownloadRepository(
        radioDownloadRepository: RadioDownloadRepositoryImpl
    ): RadioDownloadRepository

    @Binds
    @Singleton
    abstract fun bindFbDatabaseRepository(
        fbDatabaseRepository: FbDatabaseRepositoryImpl
    ): FbDatabaseRepository

    @Binds
    @Singleton
    abstract fun bindFbStorageRepository(
        fbStorageRepository: FbStorageRepositoryImpl
    ): FbStorageRepository
}