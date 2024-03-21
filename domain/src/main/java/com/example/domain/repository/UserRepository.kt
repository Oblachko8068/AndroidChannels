package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.User

interface UserRepository {

    fun getUsersDataFromRoom(): LiveData<List<User>>

    suspend fun setUsersDataToRoom(user: User)
}