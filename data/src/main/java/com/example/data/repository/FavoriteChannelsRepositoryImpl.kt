package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.model.FavoriteChannelDbEntity
import com.example.data.room.FavoriteChannelDao
import com.example.domain.model.FavoriteChannel
import com.example.domain.repository.FavoriteChannelsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteChannelsRepositoryImpl @Inject constructor(
    private val favoriteChannelDao: FavoriteChannelDao
) : FavoriteChannelsRepository {

    override fun getFavoriteChannelListLiveData(): LiveData<List<FavoriteChannel>> {
        return favoriteChannelDao.getAllFavoriteChannelList()
            .map { favoriteChannelDbEntities -> favoriteChannelDbEntities.map { it.toFavoriteChannelDb() } }
    }

    override fun addChannelFromFavoriteChannels(channelId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteChannelDbEntity = FavoriteChannelDbEntity(channelId, true)
            favoriteChannelDao.createFavoriteChannels(favoriteChannelDbEntity)
        }
    }

    override fun removeChannelFromFavoriteChannels(channelId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val favoriteChannelDbEntity = FavoriteChannelDbEntity(channelId, true)
            favoriteChannelDao.deleteFavoriteChannel(favoriteChannelDbEntity)
        }
    }
}
