package com.example.channels.retrofit

data class ChannelsJson(
    val channels: List<ChannelJson>
)

data class ChannelJson(
    val id: Int,
    val name: String,
    val image: String,
    val epg: List<EpgJson>,
    val stream: String
)

data class EpgJson(
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
)
