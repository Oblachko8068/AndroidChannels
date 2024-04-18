package com.example.domain.model

import java.io.Serializable

data class Epg(
    val channelID: Int,
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) : Serializable
