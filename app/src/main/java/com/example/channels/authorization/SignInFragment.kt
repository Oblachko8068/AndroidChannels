package com.example.channels.authorization

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.databinding.FragmentSignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.database

const val VERIFICATION_ID = "VERIFICATION_ID"
const val PHONE_NUMBER = "PHONE_NUMBER"

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private var database = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            val sendedCode = arguments?.getString(VERIFICATION_ID)
            val phoneNumber = arguments?.getString(PHONE_NUMBER)
            val inputCode = binding.codeInput.text.toString()
            val credential = sendedCode?.let { PhoneAuthProvider.getCredential(it, inputCode) }
            AUTH.signInWithCredential(credential!!).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val uid = AUTH.currentUser?.uid.toString()
                    val dateMap = mutableMapOf<String, Any>()
                    dateMap["id"] = uid
                    dateMap["phone"] = phoneNumber as String
                    dateMap["user_name"] = uid

                    //запись в бд не работает
                    database.child("users").child(uid).updateChildren(dateMap)

                    val resultData = Bundle()
                    resultData.putInt("signedIn", 1)
                    setFragmentResult(SIGN_IN_GOOD, resultData)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()

                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
            }
        }
    }

    companion object{
        fun newInstance(verificationId: String, phoneNumber: String): SignInFragment{
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    putString(VERIFICATION_ID, verificationId)
                    putString(PHONE_NUMBER, phoneNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}