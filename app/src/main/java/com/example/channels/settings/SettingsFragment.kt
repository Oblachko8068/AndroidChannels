package com.example.channels.settings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.channels.R
import com.example.channels.authorization.DB_REF
import com.example.channels.authorization.USER_VIEW_MODEL
import com.example.channels.databinding.FragmentSettingsBinding
import com.example.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

const val USER_INFO_CHANGED = "USER_INFO_CHANGED"

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
        val user: LiveData<List<User>> = USER_VIEW_MODEL.getUserData()
        val userInfo = user.value?.lastOrNull()
        applyHeader(userInfo)

        binding.changeUserInfo.setOnClickListener {
            startAnimation(it)
            if (userInfo != null) {
                val dialogFragment =
                    ChangeUserInfoDialog.newInstance(userInfo.displayName, userInfo.image, userInfo.id)
                dialogFragment.show(parentFragmentManager, "user_settings")
            }
        }

        parentFragmentManager.setFragmentResultListener(USER_INFO_CHANGED, viewLifecycleOwner) { _, res ->
            val newDisplayName = res.getString("fullName")
            binding.profileName.text = newDisplayName
            val newImage = res.getString("imageUri")
            if (newImage.isNullOrBlank()){
                userInfo?.image?.let { setUserImage(it) }
            } else {
                setUserImage(newImage)
            }
            userInfo?.let { updateUserInfo(newDisplayName, newImage, it) }
        }
    }

    private fun updateUserInfo(newDisplayName: String?, newImage: String?, userInfo: User) {
        val newUserInfo = User(
            id = userInfo.id,
            displayName = newDisplayName.toString(),
            phone = userInfo.phone,
            email = userInfo.email,
            google = userInfo.google,
            image = newImage.toString(),
            subscription = userInfo.subscription
        )
        USER_VIEW_MODEL.saveUser(newUserInfo)
        updateFirebaseUserInfo(userInfo.id, newDisplayName, newImage)
    }

    private fun updateFirebaseUserInfo(uid: String, newDisplayName: String?, newImage: String?) {
        DB_REF.child("users").child(uid).child("displayName").setValue(newDisplayName.toString())
        DB_REF.child("users").child(uid).child("image").setValue(newImage.toString())
    }

    private fun applyHeader(userInfo: User?) {
        if (userInfo != null) {
            setUserImage(userInfo.image)
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

    private fun setUserImage(image: String) {
        if (image != ""){
            Glide.with(requireContext())
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profilePic)
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
            ).signOut()
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