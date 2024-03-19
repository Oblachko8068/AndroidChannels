package com.example.channels.authorization

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_USERS = "users"

fun initDatabase(){
    AUTH = Firebase.auth
    FirebaseAuth.getInstance().firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
    AUTH.setLanguageCode("ru")
}