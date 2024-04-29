package com.example.data.repository

import com.example.domain.repository.FbDatabaseRepository
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

const val USER_CHILD = "users"

class FbDatabaseRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
) : FbDatabaseRepository {

    override fun updateUserData(child1: String, child2: String, value: Any) {
        databaseReference.child(USER_CHILD).child(child1).child(child2).setValue(value)
    }

    override fun setUserData(child1: String, value: Any) {
        databaseReference.child(USER_CHILD).child(child1).setValue(value)
    }

    override fun getUserPath() = databaseReference.child(USER_CHILD)
}