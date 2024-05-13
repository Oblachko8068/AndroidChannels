package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.listener.UserViewModelProvider
import com.example.domain.model.User

interface UserRepository {

    fun getUsersDataFromRoom(): LiveData<List<User>>

    suspend fun updateUserImage(user: User, newImage: String)

    suspend fun updateUserDisplayName(user: User, newDisplayName: String)

    fun checkUserRegistration(user: User, listener: UserViewModelProvider)

    suspend fun signOut()
}