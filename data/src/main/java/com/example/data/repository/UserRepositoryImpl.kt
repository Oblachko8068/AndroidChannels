package com.example.data.repository

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.model.toDbEntity
import com.example.data.room.UserDao
import com.example.domain.listener.UserViewModelProvider
import com.example.domain.model.User
import com.example.domain.repository.FbDatabaseRepository
import com.example.domain.repository.FbStorageRepository
import com.example.domain.repository.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val fbStorageRepository: FbStorageRepository,
    private val fbDatabaseRepository: FbDatabaseRepository
) : UserRepository {

    override fun getUsersDataFromRoom(): LiveData<List<User>> =
        userDao.getUsersData().map { userDbEntities -> userDbEntities.map { it.toUserDb() } }

    private suspend fun setUsersDataToRoom(user: User) {
        withContext(Dispatchers.IO) {
            userDao.setUsersData(user.toDbEntity())
        }
    }

    override suspend fun updateUserImage(user: User, newImage: String) {
        user.image = newImage
        fbDatabaseRepository.updateUserData(user.id, "image", newImage)
        fbStorageRepository.saveUserPhoto(user.id, newImage.toUri())
        withContext(Dispatchers.IO) {
            userDao.updateUserInfo(user.toDbEntity())
        }
    }

    override suspend fun updateUserDisplayName(user: User, newDisplayName: String) {
        user.displayName = newDisplayName
        fbDatabaseRepository.updateUserData(user.id, "displayName", newDisplayName)
        withContext(Dispatchers.IO) {
            userDao.updateUserInfo(user.toDbEntity())
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            userDao.deleteAllUsers()
        }
    }

    override fun checkUserRegistration(user: User, listener: UserViewModelProvider) {
        fbDatabaseRepository.getUserPath().child(user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(User::class.java)
                        if (userData != null) {
                            saveUserToRoom(userData)
                            listener.signInIsGood()
                        } else {
                            saveUser(user)
                        }
                    } else {
                        saveUser(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun saveUser(user: User) {
        saveToFirebase(user)
        makeUserToSave(user)
    }

    private fun saveToFirebase(user: User) {
        val userDateMap = mutableMapOf<String, Any>()
        userDateMap["id"] = user.id
        userDateMap["displayName"] = user.displayName
        userDateMap["phone"] = user.phone
        userDateMap["email"] = user.email
        userDateMap["google"] = user.google
        userDateMap["image"] = ""
        userDateMap["subscription"] = false
        fbDatabaseRepository.setUserData(user.id, userDateMap)
    }

    private fun makeUserToSave(user: User) {
        val userToSave = User(
            id = user.id,
            displayName = user.displayName,
            phone = user.phone,
            email = user.email,
            google = user.google,
            image = "",
            subscription = false
        )
        saveUserToRoom(userToSave)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveUserToRoom(user: User) {
        GlobalScope.launch {
            setUsersDataToRoom(user)
        }
    }
}