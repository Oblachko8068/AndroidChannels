package com.example.channels.authorization

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.channels.R
import com.example.channels.databinding.FragmentLoginBinding

const val SIGN_IN_GOOD = "SIGN_IN_GOOD"
const val SIGN_UP_GOOD = "SIGN_UP_GOOD"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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

        binding.nextButton.setOnClickListener {
            //запрос в бд
            binding.progressBar.visibility = View.VISIBLE
            binding.nextButton.visibility = View.INVISIBLE
            Handler().postDelayed({
                val userIsFound = true
                if (userIsFound){
                    launchFragment(SignInFragment.newInstance())
                } else {
                    launchFragment(SignUpFragment())
                }
            }, 1000)

        }

        parentFragmentManager.setFragmentResultListener(SIGN_IN_GOOD, viewLifecycleOwner) { _, res ->
            if (res.getInt("signedIn") == 1){
                Toast.makeText(requireContext(), "Вы вошли", Toast.LENGTH_SHORT).show()
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "пользователь не вошел", Toast.LENGTH_SHORT).show()
            }
        }
        parentFragmentManager.setFragmentResultListener(SIGN_UP_GOOD, viewLifecycleOwner) { _, res ->
            if (res.getInt("signedUp") == 1){
                Toast.makeText(requireContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show()
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "пользователь не зарегестрировался", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchFragment(fragment: Fragment){
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}