package com.example.domain.model

import java.io.Serializable

data class Channel(
    val id: Int,
    val name: String,
    val image: String,
    val stream: String
) : Serializable