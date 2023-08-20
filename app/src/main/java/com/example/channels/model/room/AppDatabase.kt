package com.example.channels.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

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