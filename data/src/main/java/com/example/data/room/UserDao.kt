package com.example.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserDbEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getUsersData(): LiveData<List<UserDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setUsersData(userDbEntity: UserDbEntity)

    @Query("DELETE FROM users")
    fun deleteAllUsers()

    @Update
    fun updateUserInfo(userDbEntity: UserDbEntity)
}