package com.example.channels.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.channels.model.retrofit.ChannelDb

@Entity(
    tableName = "channels"
)
data class ChannelDbEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val stream: String
) {

    fun toChannelDb(): ChannelDb = ChannelDb(
        id = id,
        name = name,
        image = image,
        stream = stream
    )

    companion object{
        fun fromChannelDb(channelDb: ChannelDb): ChannelDbEntity = ChannelDbEntity(
            id = channelDb.id,
            name = channelDb.name,
            image = channelDb.image,
            stream = channelDb.stream
        )
    }
}
