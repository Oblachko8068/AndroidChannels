package com.example.channels.authorization

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
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

const val VERIFICATION_ID = "VERIFICATION_ID"

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            val sendedCode = arguments?.getString(VERIFICATION_ID)
            val inputCode = binding.codeInput.text.toString()
            val credential = sendedCode?.let { PhoneAuthProvider.getCredential(it, inputCode) }
            auth.signInWithCredential(credential!!).addOnCompleteListener {
                if (it.isSuccessful){
                    val resultData = Bundle()
                    resultData.putInt("signedIn", 1)
                    setFragmentResult(SIGN_IN_GOOD, resultData)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                } else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.w(TAG, "signInWithCredential:failure", it.exception)
                    }
                }
            }
        }
    }

    companion object{
        fun newInstance(verificationId: String): SignInFragment{
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    putString(VERIFICATION_ID, verificationId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}