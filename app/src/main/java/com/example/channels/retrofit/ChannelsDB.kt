package com.example.channels.retrofit

import java.io.Serializable

data class ChannelDB(
    val id: Int,
    val name: String,
    val image: String,
    val stream: String
) : Serializable

data class EpgDB(
    val channelID: Int,
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) : Serializable

fun ChannelJSON.toChannelDB() = ChannelDB(
    id = this.id,
    name = this.name,
    image = this.image,
    stream = this.stream,
)

fun ChannelJSON.toEpgDB(): List<EpgDB> = this.epg.map {
    EpgDB(
        this.id,
        id = it.id,
        timestart = it.timestart,
        timestop = it.timestop,
        title = it.title
    )
}