package com.example.channels.retrofit

data class ChannelsNew(
    val channels: List<Channel>
)

data class Channel(
    val id: Int,
    val name: String,
    val image: String,
    val epg: List<EPG>,
    val stream: String
)

data class EPG(
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
)

