package com.example.domain.repository

import com.google.firebase.database.DatabaseReference

interface FbDatabaseRepository {

    fun updateUserData(child1: String, child2: String, value: Any)

    fun setUserData(child1: String, value: Any)

    fun getUserPath(): DatabaseReference

}