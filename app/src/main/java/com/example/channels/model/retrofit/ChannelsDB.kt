package com.example.channels.model.retrofit

import java.io.Serializable

data class Channel(
    val id: Int,
    val name: String,
    val image: String,
    val stream: String
) : Serializable

data class Epg(
    val channelID: Int,
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) : Serializable
