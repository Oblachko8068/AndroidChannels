package com.example.channels.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var userLiveData: LiveData<List<User>> = userRepository.getUsersDataFromRoom()

    fun saveUser(user: User){
        viewModelScope.launch {
            userRepository.setUsersDataToRoom(user)
        }
    }

    fun getUserData() = userLiveData
}