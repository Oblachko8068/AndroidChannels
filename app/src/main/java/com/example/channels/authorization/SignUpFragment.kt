package com.example.channels.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.databinding.FragmentSignUpBinding
import com.example.domain.model.User

const val UID = "UID"
const val USER_PHONE = "USER_PHONE"
const val DISPLAY_NAME = "DISPLAY_NAME"
const val EMAIL = "EMAIL"
const val BY_GOOGLE = "BY_GOOGLE"

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getString(UID).toString()
        val phoneNumber = arguments?.getString(USER_PHONE).toString()
        val displayName = arguments?.getString(DISPLAY_NAME).toString()
        val email = arguments?.getString(EMAIL).toString()
        val google = arguments?.getBoolean(BY_GOOGLE)
        if (displayName != ""){
            val parts = displayName.split(" ")
            binding.inputName.setText(parts[0])
            binding.inputSirname.setText(parts[1])
        }

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            if (binding.inputName.text.isNotEmpty()) {
                val fullName = "${binding.inputName.text} ${binding.inputSirname.text}"
                saveToFirebase(uid, fullName, phoneNumber, email, google)
                saveToRoom(uid, fullName, phoneNumber, email, google)
                submitResult()
            } else {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun submitResult() {
        val resultData = Bundle()
        resultData.putInt("signedUp", 1)
        setFragmentResult(SIGN_UP_GOOD, resultData)
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun saveToRoom(
        uid: String,
        fullName: String,
        phoneNumber: String,
        email: String,
        google: Boolean?
    ) {
        val user = User(
            id = uid,
            displayName = fullName,
            phone = phoneNumber,
            email = email,
            google = google as Boolean,
            image = "",
            subscription = false
        )
        USER_VIEW_MODEL.saveUser(user)
    }

    private fun saveToFirebase(
        uid: String,
        fullName: String,
        phoneNumber: String,
        email: String,
        google: Boolean?
    ) {
        val userDateMap = mutableMapOf<String, Any>()
        userDateMap["id"] = uid
        userDateMap["displayName"] = fullName
        userDateMap["phone"] = phoneNumber
        userDateMap["email"] = email
        userDateMap["google"] = google as Boolean
        userDateMap["image"] = ""
        userDateMap["subscription"] = false
        DB_REF.child("users").child(uid).setValue(userDateMap)
    }

    companion object {
        fun newInstance(
            uid: String?,
            phoneNumber: String,
            displayName: String,
            email: String,
            google: Boolean
        ): SignUpFragment {
            return SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(UID, uid)
                    putString(USER_PHONE, phoneNumber)
                    putString(DISPLAY_NAME, displayName)
                    putString(EMAIL, email)
                    putBoolean(BY_GOOGLE, google)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}