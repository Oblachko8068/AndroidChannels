package com.example.channels.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.USER_VIEW_MODEL
import com.example.channels.databinding.FragmentLoginBinding

const val SIGN_UP_GOOD = "SIGN_UP_GOOD"

class LoginFragment : Fragment(), UserViewModelProvider {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isCodeRequest = false
    private lateinit var verificationCode: String
    private val googleResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            USER_VIEW_MODEL.authWithGoogle(it.data)
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
        USER_VIEW_MODEL.setListener(this)

        binding.googleRegistration.setOnClickListener {
            googleResultLauncher.launch(USER_VIEW_MODEL.getGoogleSignInIntent())
        }

        binding.nextButton.setOnClickListener {
            val phoneNumber = binding.loginInput.text.toString()
            if (!isCodeRequest) {
                if (phoneNumber.isEmpty()) {
                    showToast("Введите данные для входа")
                } else if (phoneNumber.length != 12) {
                    showToast("Недопустимая длина номера телефона")
                } else {
                    showProgressBar()
                    USER_VIEW_MODEL.generatePhoneCode(phoneNumber, requireActivity())
                }
            } else {
                val inputCode = binding.codeInput.text.toString()
                if (inputCode.length == 6) {
                    showProgressBar()
                    USER_VIEW_MODEL.authWithPhone(verificationCode, inputCode, phoneNumber)
                } else {
                    showButtonNext()
                    showToast("Длина кода 6 символов")
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            SIGN_UP_GOOD,
            viewLifecycleOwner
        ) { _, res ->
            if (res.getInt("signedUp") == 1) {
                showToast("Вы зарегистрировались")
                fragmentPopBackStack()
            } else {
                showToast("Ошибка регистрации, повторите попытку")
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.nextButton.visibility = View.INVISIBLE
    }

    private fun showButtonNext() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.nextButton.visibility = View.VISIBLE
    }

    override fun signInIsGood() {
        fragmentPopBackStack()
    }

    override fun signUpUser() {
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, SignUpFragment())
            .commit()
    }

    override fun errorOccurred(message: String) {
        showToast(message)
    }

    override fun codeSend(id: String) {
        binding.codeInput.visibility = View.VISIBLE
        binding.signInText.visibility = View.VISIBLE
        showButtonNext()
        verificationCode = id
    }

    override fun phoneVerificationFailed() {
        showButtonNext()
        showToast("Проверьте номер телефона")
    }

    private fun fragmentPopBackStack() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}