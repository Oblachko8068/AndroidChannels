package com.example.channels.authorization

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.databinding.FragmentSignUpBinding

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

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            val resultData = Bundle()
            resultData.putInt("signedUp", 1)
            setFragmentResult(SIGN_UP_GOOD, resultData)
            Handler().postDelayed({
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }, 1000)
        }
    }

    companion object{
        fun newInstance(): SignUpFragment{
            return SignUpFragment().apply {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}