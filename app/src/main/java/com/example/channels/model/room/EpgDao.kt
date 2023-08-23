package com.example.channels.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.channels.model.retrofit.EpgDb
import kotlinx.coroutines.flow.Flow

@Dao
interface EpgDao {

   /* @Query("SELECT channel_id, id, title FROM epgs")
    suspend fun findViewingTheEpgTitleTuple(): ViewingTheEpgTitleTuple?*/

    @Query("SELECT * FROM epgs")
    fun getEpgListAll(): List<EpgDbEntity?>

    @Query("SELECT * FROM epgs")
    fun getEpgListAllTestVersion(): LiveData<List<EpgDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEpg(epgDbEntity: EpgDbEntity)
}