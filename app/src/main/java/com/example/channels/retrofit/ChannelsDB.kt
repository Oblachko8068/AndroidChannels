package com.example.channels.retrofit

import java.io.Serializable

data class ChannelDb(
    val id: Int,
    val name: String,
    val image: String,
    val stream: String
) : Serializable

data class EpgDb(
    val channelID: Int,
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) : Serializable

fun ChannelJson.toChannelDb() = ChannelDb(
    id = this.id,
    name = this.name,
    image = this.image,
    stream = this.stream,
)

fun ChannelJson.toEpgDb(): List<EpgDb> = this.epg.map {
    EpgDb(
        this.id,
        id = it.id,
        timestart = it.timestart,
        timestop = it.timestop,
        title = it.title
    )
}