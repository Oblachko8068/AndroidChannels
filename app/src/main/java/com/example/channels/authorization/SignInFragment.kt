package com.example.channels.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.databinding.FragmentSignInBinding
import com.example.domain.model.User
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

const val VERIFICATION_ID = "VERIFICATION_ID"

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

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
            val inputCode = binding.codeInput.text.toString()
            if (inputCode.length != 6) {
                binding.progressBar.visibility = View.VISIBLE
                binding.submit.visibility = View.INVISIBLE
                val sendedCode = arguments?.getString(VERIFICATION_ID)
                val credential = sendedCode?.let { PhoneAuthProvider.getCredential(it, inputCode) }
                AUTH.signInWithCredential(credential!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserRegistration()
                    } else {
                        Toast.makeText(requireContext(), "Проверьте код", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Проверьте код", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkUserRegistration() {
        val resultData = Bundle()
        val uid = AUTH.currentUser?.uid.toString()
        DB_REF.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(User::class.java)
                        if (userData != null) {
                            USER_VIEW_MODEL.saveUser(userData)
                        }
                        resultData.putInt("signedIn", 1)
                    } else {
                        resultData.putInt("signedIn", 0)
                        resultData.putString("uid", uid)
                    }
                    setFragmentResult(SIGN_IN_GOOD, resultData)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        fun newInstance(verificationId: String): SignInFragment {
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