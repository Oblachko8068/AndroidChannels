package com.example.domain.model

data class User(
    var id: String = "",
    var displayName: String = "",
    var phone: String = "",
    var email: String = "",
    var google: Boolean = false,
    var image: String = "",
    val subscription: Boolean = false,
)