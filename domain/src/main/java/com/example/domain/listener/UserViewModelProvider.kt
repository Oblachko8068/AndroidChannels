package com.example.domain.listener

interface UserViewModelProvider {

    fun signInIsGood()

    fun errorOccurred(message: String)

    fun codeSend(id: String)
}