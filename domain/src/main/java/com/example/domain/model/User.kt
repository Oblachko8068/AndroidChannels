package com.example.domain.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val google: Boolean = false,
    val image: String = "",
    val subscription: Boolean = false,
)