package com.example.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.EpgDbEntity

@Dao
interface EpgDao {

    @Query("SELECT * FROM epgs")
    fun getEpgListAll(): LiveData<List<EpgDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEpg(epgDbEntity: List<EpgDbEntity>)

    @Query("SELECT * FROM epgs WHERE channelID = :channelID")
    fun getEpgByChannelID(channelID: Int): LiveData<EpgDbEntity>
}