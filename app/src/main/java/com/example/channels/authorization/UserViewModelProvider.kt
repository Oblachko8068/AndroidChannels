package com.example.channels.authorization

interface UserViewModelProvider {

    fun signInIsGood()

    fun signUpUser()

    fun errorOccurred(message: String)

    fun codeSend(id: String)

    fun phoneVerificationFailed()
}