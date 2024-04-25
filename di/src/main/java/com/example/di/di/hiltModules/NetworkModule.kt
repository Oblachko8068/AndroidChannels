package com.example.di.di.hiltModules

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabaseReference(): DatabaseReference {
        val database =
            FirebaseDatabase.getInstance("https://channels-41585-default-rtdb.europe-west1.firebasedatabase.app/")
        return database.getReference()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageReference(): StorageReference {
        val storage = FirebaseStorage.getInstance("gs://channels-41585.appspot.com")
        return storage.getReference()
    }
}
