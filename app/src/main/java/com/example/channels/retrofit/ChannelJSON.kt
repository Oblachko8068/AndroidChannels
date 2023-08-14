package com.example.channels.retrofit

data class ChannelsJSON(
    val channels: List<ChannelJSON>
)

data class ChannelJSON(
    val id: Int,
    val name: String,
    val image: String,
    val epg: List<EpgJSON>,
    val stream: String
)

data class EpgJSON(
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
)
