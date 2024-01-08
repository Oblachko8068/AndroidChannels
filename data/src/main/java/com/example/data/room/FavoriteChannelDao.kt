package com.example.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FavoriteChannelDbEntity

@Dao
interface FavoriteChannelDao {

    @Query("SELECT * FROM favorite_channels")
    fun getAllFavoriteChannelList(): LiveData<List<FavoriteChannelDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createFavoriteChannels(favoriteChannelDbEntity: FavoriteChannelDbEntity)

    @Delete
    fun deleteFavoriteChannel(favoriteChannelDbEntity: FavoriteChannelDbEntity)
}