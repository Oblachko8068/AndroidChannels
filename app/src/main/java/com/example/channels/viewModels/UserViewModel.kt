package com.example.channels.viewModels

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.listener.UserViewModelProvider
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private var listener: UserViewModelProvider? = null
    private var userLiveData: LiveData<List<User>> = userRepository.getUsersDataFromRoom()

    fun getUserData() = userLiveData

    fun setListener(listener: UserViewModelProvider) {
        this.listener = listener
    }

    fun authWithGoogle(data: Intent?) {
        listener?.let {
            authRepository.authWithGoogle(data, it)
        }
    }

    fun getGoogleSignInIntent() = authRepository.getGoogleSignInIntent()

    fun generatePhoneCode(phoneNumber: String, requireActivity: FragmentActivity) {
        listener?.let {
            authRepository.generatePhoneCode(phoneNumber, requireActivity, it)
        }
    }

    fun authWithPhone(sendCode: String, inputCode: String, phoneNumber: String) {
        listener?.let {
            authRepository.authWithPhone(sendCode, inputCode, phoneNumber, it)
        }
    }

    fun signOutFromAccount() {
        authRepository.signOut()
        viewModelScope.launch {
            userRepository.deleteUser()
        }
    }

    fun updateUserPhoto(user: User, newImage: String) {
        viewModelScope.launch {
            userRepository.updateUserImage(user, newImage)
        }
    }

    fun updateUserDisplayName(user: User, newDisplayName: String) {
        viewModelScope.launch {
            userRepository.updateUserDisplayName(user, newDisplayName)
        }
    }
}