package com.example.data.model

data class ChannelJson(
    val id: Int,
    val name: String,
    val image: String,
    val epg: List<EpgJson>,
    val stream: String
)
