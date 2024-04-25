package com.example.data.repository

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.example.domain.listener.UserViewModelProvider
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val userRepository: UserRepository
) : AuthRepository {

    override fun authWithGoogle(data: Intent?, listener: UserViewModelProvider) {
        val signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (signInAccountTask.isSuccessful) {
            try {
                val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                    handleSignInResult(task, googleSignInAccount, true, listener)
                }
            } catch (e: ApiException) {
                listener.errorOccurred(e.message.toString())
            }
        }
    }

    private fun handleSignInResult(
        task: Task<AuthResult>,
        googleSignInAccount: GoogleSignInAccount? = null,
        isGoogle: Boolean,
        listener: UserViewModelProvider,
    ) {
        if (task.isSuccessful) {
            val user: User = if (isGoogle) {
                User(
                    id = task.result.user?.uid.toString(),
                    displayName = googleSignInAccount?.displayName ?: "",
                    email = googleSignInAccount?.email.toString(),
                    google = true
                )
            } else {
                User(
                    id = task.result.user?.uid.toString(),
                    phone = task.result.user?.phoneNumber.toString()
                )
            }
            userRepository.checkUserRegistration(user, listener)
        } else {
            listener.errorOccurred("Ошибка авторизации, повторите позже")
        }
    }

    override fun authWithPhone(
        sendCode: String,
        inputCode: String,
        phoneNumber: String,
        listener: UserViewModelProvider
    ) {
        if (sendCode != "") {
            val credential = PhoneAuthProvider.getCredential(sendCode, inputCode)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                handleSignInResult(task, null, false, listener)
            }
        } else {
            listener.errorOccurred("Ошибка отправки кода, повторите позже")
        }
    }

    override fun generatePhoneCode(
        phoneNumber: String,
        requireActivity: FragmentActivity,
        listener: UserViewModelProvider
    ) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                listener.errorOccurred(e.message.toString())
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                listener.codeSend(verificationId)
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

    override fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    override fun signOut() {
        googleSignInClient.signOut()
        firebaseAuth.signOut()
    }
}