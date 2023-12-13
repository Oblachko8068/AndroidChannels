package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.FavoriteChannel

@Entity(
    tableName = "favorite_channels"
)

data class FavoriteChannelDbEntity(
    @PrimaryKey val channelId: Int,
) {
    fun toFavoriteChannelDb(): FavoriteChannel {
        return FavoriteChannel(
            channelId = channelId,
        )
    }
}