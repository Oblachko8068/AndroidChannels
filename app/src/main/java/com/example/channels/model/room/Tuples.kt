package com.example.channels.model.room

data class ViewingTheChannelListTuple(
    val id: Int,
    val name: String,
    val image: String,
)

data class ViewingTheEpgTitleTuple(
    val channelID: Int,
    val id: Long,
    val title: String,
)