package com.example.channels.authorization

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.channels.R
import com.example.channels.databinding.FragmentChangeUserInfoDialogBinding

const val NAME = "NAME"
const val IMAGE = "IMAGE"
const val CHANGE_USER_INFO = "ChangeUserInfoDialogResult"

class ChangeUserInfoDialog : DialogFragment() {

    private var _binding: FragmentChangeUserInfoDialogBinding? = null
    private val binding get() = _binding!!
    override fun getTheme() = R.style.RoundedCornersDialog
    private var changedImageUri: Uri? = null
    private var changeImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                changedImageUri = it.data?.data
                changedImageUri?.let { uri -> setImageView(uri) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUserInfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayName = arguments?.getString(NAME)
        val img = arguments?.getString(IMAGE)
        if (displayName != "" && displayName != null) {
            val parts = displayName.split(" ")
            binding.changeProfileName.setText(parts[0])
            binding.changeProfileSirname.setText(parts[1])
        }
        if (img != "" && img != null) {
            setImageView(img)
        }

        binding.changeProfileImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }

        binding.submit.setOnClickListener {
            if (binding.changeProfileName.text.isEmpty()) {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
            } else {
                val newName = "${binding.changeProfileName.text} ${binding.changeProfileSirname.text}"
                submitChanges(newName)
            }
        }

        binding.exit.setOnClickListener {
            exitFromAccount()
        }
    }

    private fun exitFromAccount() {
        val result = Bundle()
        result.putBoolean("exit", true)
        setFragmentResult(CHANGE_USER_INFO, result)
        dismiss()
    }

    private fun submitChanges(newName: String) {
        val result = Bundle()
        result.putString("fullName", newName)
        result.putString("imageUri", changedImageUri?.toString())
        setFragmentResult(CHANGE_USER_INFO, result)
        dismiss()
    }

    private fun setImageView(image: Any) {
        Glide.with(requireContext())
            .load(image)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.profilePic)
    }

    companion object {
        fun newInstance(displayName: String, image: String): ChangeUserInfoDialog {
            return ChangeUserInfoDialog().apply {
                arguments = Bundle().apply {
                    putString(NAME, displayName)
                    putString(IMAGE, image)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}