package com.example.channels.authorization

import androidx.activity.viewModels
import com.example.channels.MainActivity
import com.example.channels.viewModels.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

lateinit var AUTH: FirebaseAuth
lateinit var DATABASE: FirebaseDatabase
lateinit var DB_REF: DatabaseReference
lateinit var USER_VIEW_MODEL: UserViewModel

fun initDatabase(mainActivity: MainActivity) {
    USER_VIEW_MODEL = mainActivity.viewModels<UserViewModel>().value
    DATABASE =
        FirebaseDatabase.getInstance("https://channels-41585-default-rtdb.europe-west1.firebasedatabase.app/")
    DB_REF = DATABASE.getReference()
    AUTH = Firebase.auth
    FirebaseAuth.getInstance().firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
    AUTH.setLanguageCode("ru")
}