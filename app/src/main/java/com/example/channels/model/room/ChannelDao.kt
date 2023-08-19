package com.example.channels.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT id, name, image FROM channels")
    suspend fun findViewingTheChannelListTuple(): ViewingTheChannelListTuple?

    @Query("SELECT * FROM channels")
    fun getChannelListAll(): List<ChannelDbEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createChannel(channelDbEntity: ChannelDbEntity)
}