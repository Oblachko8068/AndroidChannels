package com.example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.ChannelDbEntity
import com.example.data.model.EpgDbEntity
import com.example.data.model.FavoriteChannelDbEntity
import com.example.data.model.UserDbEntity
import com.example.data.model.RadioDbEntity

@Database(
    version = 2,
    entities = [
        ChannelDbEntity::class,
        EpgDbEntity::class,
        FavoriteChannelDbEntity::class,
        UserDbEntity::class,
        RadioDbEntity::class
    ]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getChannelDao(): ChannelDao

    abstract fun getEpgDao(): EpgDao

    abstract fun getFavoriteChannelDao(): FavoriteChannelDao

    abstract fun getUserDao(): UserDao

    abstract fun getRadioDao(): RadioDao
}