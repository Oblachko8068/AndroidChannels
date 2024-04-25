package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.listener.UserViewModelProvider
import com.example.domain.model.User

interface UserRepository {

    fun getUsersDataFromRoom(): LiveData<List<User>>

    suspend fun setUsersDataToRoom(user: User)

    suspend fun updateUserImage(user: User, newImage: String)

    suspend fun deleteUser()

    suspend fun updateUserDisplayName(user: User, newDisplayName: String)

    fun saveUserToRoom(user: User)

    fun saveUser(user: User)

    fun checkUserRegistration(user: User, listener: UserViewModelProvider)
}