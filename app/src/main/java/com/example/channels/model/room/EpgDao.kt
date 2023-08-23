package com.example.channels.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.channels.model.retrofit.EpgDb

@Dao
interface EpgDao {

    @Query("SELECT * FROM epgs")
    fun getEpgListAll(): LiveData<List<EpgDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEpg(epgDbEntity: List<EpgDbEntity>)
}