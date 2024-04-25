package com.example.domain.repository

import android.net.Uri

interface FbStorageRepository {

    fun saveUserPhoto(uid: String, uri: Uri)
}