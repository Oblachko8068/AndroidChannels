package com.example.domain.model

import java.io.Serializable

data class Radio (
    val id: Int = 0,
    val name: String = "",
    val image: String = "",
    val stream: String = ""
) : Serializable