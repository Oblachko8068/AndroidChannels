package com.example.channels.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.channels.model.retrofit.ChannelDb

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels")
    fun getChannelListAll(): LiveData<List<ChannelDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createChannel(channelDbEntity: List<ChannelDbEntity>)
}