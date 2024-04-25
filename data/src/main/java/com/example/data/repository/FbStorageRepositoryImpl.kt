package com.example.data.repository

import android.net.Uri
import com.example.domain.repository.FbStorageRepository
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

const val USER_PROFILE_IMAGE = "profile_image"

class FbStorageRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference
) : FbStorageRepository {

    override fun saveUserPhoto(uid: String, uri: Uri) {
        storageReference.child(USER_PROFILE_IMAGE).child(uid).putFile(uri)
    }
}