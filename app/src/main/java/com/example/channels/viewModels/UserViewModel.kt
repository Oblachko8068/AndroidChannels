package com.example.channels.viewModels

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.channels.authorization.UserViewModelProvider
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val FOLDER_PROFILE_IMAGE = "profile_image"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private var userLiveData: LiveData<List<User>> = userRepository.getUsersDataFromRoom()
    private var userToSignUp = User()
    private var listener: UserViewModelProvider? = null

    fun getUserName() = userToSignUp.displayName

    fun getUserData() = userLiveData

    fun setListener(listener: UserViewModelProvider) {
        this.listener = listener
    }

    fun getStorage(): StorageReference = storageReference

    fun authWithGoogle(data: Intent?) {
        val signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (signInAccountTask.isSuccessful) {
            try {
                val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userToSignUp.id = task.result.user?.uid.toString()
                        userToSignUp.displayName = googleSignInAccount.displayName.toString()
                        userToSignUp.email = googleSignInAccount.email.toString()
                        checkUserRegistration()
                        userToSignUp.google = true
                    } else {
                        listener?.errorOccurred("Ошибка авторизации, повторите позже")
                    }
                }
            } catch (e: ApiException) {
                listener?.errorOccurred(e.message.toString())
            }
        }
    }

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun generatePhoneCode(
        phoneNumber: String,
        requireActivity: FragmentActivity
    ) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                listener?.phoneVerificationFailed()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                listener?.codeSend(verificationId)
            }
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun authWithPhone(sendedCode: String?, inputCode: String, phoneNumber: String) {
        val credential = sendedCode?.let { PhoneAuthProvider.getCredential(it, inputCode) }
        firebaseAuth.signInWithCredential(credential!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userToSignUp.phone = phoneNumber
                userToSignUp.id = task.result.user?.uid.toString()
                checkUserRegistration()
            } else {
                listener?.errorOccurred("Ошибка авторизации, повторите позже")
            }
        }
    }

    private fun checkUserRegistration() {
        databaseReference.child("users").child(userToSignUp.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(User::class.java)
                        if (userData != null) {
                            saveUserToRoom(userData)
                            listener?.signInIsGood()
                        } else {
                            listener?.signUpUser()
                        }
                    } else {
                        listener?.signUpUser()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun saveUser(fullName: String) {
        saveToFirebase(fullName)
        makeUserToSave(fullName)
    }

    private fun saveToFirebase(fullName: String) {
        val userDateMap = mutableMapOf<String, Any>()
        userDateMap["id"] = userToSignUp.id
        userDateMap["displayName"] = fullName
        userDateMap["phone"] = userToSignUp.phone
        userDateMap["email"] = userToSignUp.email
        userDateMap["google"] = userToSignUp.google
        userDateMap["image"] = ""
        userDateMap["subscription"] = false
        databaseReference.child("users").child(userToSignUp.id).setValue(userDateMap)
    }

    private fun makeUserToSave(fullName: String) {
        val user = User(
            id = userToSignUp.id,
            displayName = fullName,
            phone = userToSignUp.phone,
            email = userToSignUp.email,
            google = userToSignUp.google,
            image = "",
            subscription = false
        )
        saveUserToRoom(user)
    }

    private fun saveUserToRoom(user: User) {
        viewModelScope.launch {
            userRepository.setUsersDataToRoom(user)
        }
    }

    fun signOutFromAccount() {
        if (userLiveData.value?.last()?.google == true) {
            googleSignInClient.signOut()
        }
        deleteUser()
        firebaseAuth.signOut()
    }

    private fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUser()
        }
    }
}