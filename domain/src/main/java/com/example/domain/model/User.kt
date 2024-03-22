package com.example.domain.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val image: Int = 0,
    val subscription: Boolean = false,
)