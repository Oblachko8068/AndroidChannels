package com.example.channels.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.channels.R
import com.example.channels.authorization.USER_VIEW_MODEL
import com.example.channels.databinding.FragmentSettingsBinding
import com.example.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyHeader()
    }

    private fun applyHeader() {
        val user: LiveData<List<User>> = USER_VIEW_MODEL.getUserData()
        val userInfo = user.value?.lastOrNull()
        if (userInfo != null) {
            binding.profileName.text = userInfo.displayName
            binding.exitFromProfileButton.setOnClickListener {
                startAnimation(it)
                signOutFromAccount(userInfo.google)
            }
        } else {
            binding.profileInfo.visibility = View.GONE
            binding.exitFromProfileButton.visibility = View.GONE
        }
    }

    private fun startAnimation(view: View) {
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_animation)
        view.startAnimation(scaleAnimation)
    }

    private fun signOutFromAccount(google: Boolean) {
        if (google) {
            GoogleSignIn.getClient(
                requireActivity() as Activity,
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).signOut().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Пользователь вышел", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        USER_VIEW_MODEL.deleteUser()
        Firebase.auth.signOut()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}