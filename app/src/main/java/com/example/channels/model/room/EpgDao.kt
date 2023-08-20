package com.example.channels.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EpgDao {

   /* @Query("SELECT channel_id, id, title FROM epgs")
    suspend fun findViewingTheEpgTitleTuple(): ViewingTheEpgTitleTuple?*/

    @Query("SELECT * FROM epgs")
    fun getEpgListAll(): List<EpgDbEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createEpg(epgDbEntity: EpgDbEntity)
}