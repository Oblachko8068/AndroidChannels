package com.example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.ChannelDbEntity
import com.example.data.model.EpgDbEntity

@Database(
    version = 1,
    entities = [
        ChannelDbEntity::class,
        EpgDbEntity::class
    ]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getChannelDao() : ChannelDao

    abstract fun getEpgDao() : EpgDao
}