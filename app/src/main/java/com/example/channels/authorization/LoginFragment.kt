package com.example.channels.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentLoginBinding
import com.example.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

const val SIGN_IN_GOOD = "SIGN_IN_GOOD"
const val SIGN_UP_GOOD = "SIGN_UP_GOOD"
const val GOOGLE_TOKEN = "272775250621-523c05jdaabblc837u7qh92tmvd220vu.apps.googleusercontent.com"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneNumber: String
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            signInWithGoogle(it.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleSignIn()

        binding.nextButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.nextButton.visibility = View.INVISIBLE
            if (binding.loginInput.text.toString().isEmpty()) showToast("Введите данные для входа")
            else phoneSignIn()
        }

        parentFragmentManager.setFragmentResultListener(
            SIGN_IN_GOOD,
            viewLifecycleOwner
        ) { _, res ->
            if (res.getInt("signedIn") == 1) {
                showToast("Вы вошли")
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else if (res.getInt("signedIn") == 0) {
                val uid = res.getString("uid")
                launchFragment(SignUpFragment.newInstance(uid, phoneNumber, "", "", false))
            }
        }

        parentFragmentManager.setFragmentResultListener(
            SIGN_UP_GOOD,
            viewLifecycleOwner
        ) { _, res ->
            if (res.getInt("signedUp") == 1) {
                showToast("Вы зарегистрировались")
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                showToast("Ошибка регистрации, повторите попытку")
            }
        }
    }

    private fun googleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_TOKEN)
            .requestEmail()
            .build()
        googleSignInClient =
            GoogleSignIn.getClient(requireActivity() as Activity, googleSignInOptions)
        binding.googleRegistration.setOnClickListener {
            googleResultLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun signInWithGoogle(data: Intent?) {
        val signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (signInAccountTask.isSuccessful) {
            try {
                val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                authWithGoogle(googleSignInAccount)
            } catch (e: ApiException) {
                showToast(e.message.toString())
            }
        }
    }

    private fun authWithGoogle(googleSignInAccount: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        AUTH.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = task.result.user?.uid.toString()
                val displayName = googleSignInAccount.displayName.toString()
                val email = googleSignInAccount.email.toString()
                checkUserInData(uid, displayName, email, true)
            } else {
                showToast("Ошибка с регистрацией пользователя, попробуйте позже")
            }
        }
    }

    private fun checkUserInData(uid: String, displayName: String, email: String, google: Boolean) {
        DB_REF.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(User::class.java)?.let { userData ->
                        USER_VIEW_MODEL.saveUser(userData)
                        fragmentPopBackStack()
                    } ?: launchFragment(
                        SignUpFragment.newInstance(uid, "", displayName, email, google)
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fragmentPopBackStack() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun phoneSignIn() {
        phoneNumber = binding.loginInput.text.toString()
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {}

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                launchFragment(SignInFragment.newInstance(verificationId))
            }
        }
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun launchFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}