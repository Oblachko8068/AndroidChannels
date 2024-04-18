package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.model.toDbEntity
import com.example.data.room.UserDao
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUsersDataFromRoom(): LiveData<List<User>> =
        userDao.getUsersData().map { userDbEntities -> userDbEntities.map { it.toUserDb() } }

    override suspend fun setUsersDataToRoom(user: User) {
        withContext(Dispatchers.IO){
            userDao.setUsersData(user.toDbEntity())
        }
    }

    override suspend fun deleteUser() {
        withContext(Dispatchers.IO){
            userDao.deleteAllUsers()
        }
    }
}