package com.example.channels.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.ChannelJson

@Entity(
    tableName = "channels"
)
data class ChannelDbEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val stream: String
) {

    fun toChannelDb(): Channel {
        return Channel(
            id = id,
            name = name,
            image = image,
            stream = stream
        )
    }

}
fun ChannelJson.fromChannelJsonToChannelDbEntity(): ChannelDbEntity = ChannelDbEntity(
    id = this.id,
    name = this.name,
    image = this.image,
    stream = this.stream
)
