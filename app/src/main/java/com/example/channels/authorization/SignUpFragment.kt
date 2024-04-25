package com.example.channels.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.USER_VIEW_MODEL
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
        val currentUserName = USER_VIEW_MODEL.getUserName()
        if (currentUserName != ""){
            val parts = currentUserName.split(" ")
            binding.inputName.setText(parts[0])
            binding.inputSirname.setText(parts[1])
        }

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.INVISIBLE
            if (binding.inputName.text.isNotEmpty()) {
                val fullName = "${binding.inputName.text} ${binding.inputSirname.text}"
                USER_VIEW_MODEL.saveUser(fullName)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}