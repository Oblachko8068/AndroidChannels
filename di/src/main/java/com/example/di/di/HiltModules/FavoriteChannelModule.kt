package com.example.di.di.HiltModules

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.data.repository.FavoriteChannelsRepositoryImpl
import com.example.domain.repository.FavoriteChannelsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FavoriteChannelsModule {

    @Provides
    fun provideContext(fragment: Fragment): Context {
        return fragment.requireContext()
    }

    @Provides
    fun provideFavoriteChannelsRepository(
        context: Context
    ): FavoriteChannelsRepository {
        return FavoriteChannelsRepositoryImpl(context)
    }
}