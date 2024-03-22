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

        val uid = arguments?.getString(UID)
        val phoneNumber = arguments?.getString(USER_PHONE)

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            if (binding.inputName.text.isNotEmpty()) {
                if (uid != null) {
                    val fullName = "${binding.inputName.text} ${binding.inputSirname.text}"
                    saveToFirebase(uid, fullName, phoneNumber)
                    saveToRoom(uid, fullName, phoneNumber)

                    val resultData = Bundle()
                    resultData.putInt("signedUp", 1)
                    setFragmentResult(SIGN_UP_GOOD, resultData)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                }
            } else {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveToRoom(uid: String, fullName: String, phoneNumber: String?) {
        val user = User(
            id = uid,
            displayName = fullName,
            phone = phoneNumber as String,
            email = "",
            image = 0,
            subscription = false
        )
        USER_VIEW_MODEL.saveUser(user)
    }

    private fun saveToFirebase(uid: String, fullName: String, phoneNumber: String?) {
        val userDateMap = mutableMapOf<String, Any>()
        saveToFirebase(uid, fullName, phoneNumber)
        userDateMap["id"] = uid
        userDateMap["displayName"] = fullName
        userDateMap["phone"] = phoneNumber as String
        userDateMap["email"] = ""
        userDateMap["image"] = 0
        userDateMap["subscription"] = false
        DB_REF.child("users").child(uid).setValue(userDateMap)
    }

    companion object {
        fun newInstance(uid: String?, phoneNumber: String): SignUpFragment {
            return SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(UID, uid)
                    putString(USER_PHONE, phoneNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}