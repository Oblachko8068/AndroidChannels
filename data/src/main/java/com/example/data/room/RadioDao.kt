package com.example.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChannelDbEntity
import com.example.data.model.RadioDbEntity

@Dao
interface RadioDao {

    @Query("SELECT * FROM radios")
    fun getRadioListAll(): LiveData<List<RadioDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createRadio(radioDbEntity: List<RadioDbEntity>)

}