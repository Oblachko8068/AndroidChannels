package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.FavoriteChannel

interface FavoriteChannelsRepository {

    fun getFavoriteChannelListLiveData(): LiveData<List<FavoriteChannel>>

    fun addChannelFromFavoriteChannels(channelId: Int)

    fun removeChannelFromFavoriteChannels(channelId: Int)
}