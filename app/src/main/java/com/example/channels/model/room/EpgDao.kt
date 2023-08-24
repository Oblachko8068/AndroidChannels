package com.example.channels.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.channels.model.retrofit.Epg

@Dao
interface EpgDao {

    @Query("SELECT * FROM epgs")
    fun getEpgListAll(): LiveData<List<Epg>>

    @Query("SELECT * FROM epgs")
    fun getEpgListAllTest(): LiveData<List<EpgDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEpg(epgDbEntity: List<EpgDbEntity>)
}