package com.example.channels.model.room

import android.icu.util.ULocale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.channels.model.retrofit.ChannelDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT id, name, image FROM channels")
    suspend fun findViewingTheChannelListTuple(): ViewingTheChannelListTuple?

    @Query("SELECT * FROM channels")
    fun getChannelListAll(): List<ChannelDbEntity?>

    fun getChannelListCategory(y): List<ChannelDbEntity?>

    @Query("SELECT * FROM channels")
    fun getChannelListAllTestVersion(): LiveData<List<ChannelDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createChannel(list: List<ChannelDbEntity>)
}