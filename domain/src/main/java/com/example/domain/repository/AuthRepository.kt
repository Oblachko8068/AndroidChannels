package com.example.domain.repository

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.domain.listener.UserViewModelProvider

interface AuthRepository {

    fun authWithGoogle(data: Intent?, listener: UserViewModelProvider)

    fun getGoogleSignInIntent(): Intent

    fun authWithPhone(sendCode: String, inputCode: String, phoneNumber: String, listener: UserViewModelProvider)

    fun generatePhoneCode(phoneNumber: String, requireActivity: FragmentActivity, listener: UserViewModelProvider)

    fun signOut()
}