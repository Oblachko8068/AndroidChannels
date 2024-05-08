package com.example.channels.radioPlayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.channels.R
import com.example.channels.databinding.FragmentRadioListBinding
import com.example.domain.model.Radio
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RadioListFragment : BottomSheetDialogFragment(), RadioAdapter.OnRadioItemClickListener {

    private var _binding: FragmentRadioListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRadioListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioList: ArrayList<Radio> = arguments?.getSerializable("radio") as ArrayList<Radio>
        binding.playlistRadio.setHasFixedSize(true)
        binding.playlistRadio.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRadio.adapter = RadioAdapter(requireContext(), radioList, this)
    }

    override fun onRadioItemClicked(position: Int) {
        val resultData = Bundle()
        resultData.putInt("radio_position", position)
        setFragmentResult(RADIO_POSITION_CHOOSE, resultData)
        dismiss()
    }

    companion object {
        fun newInstance(radioList: MutableList<Radio>) =
            RadioListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("radio", ArrayList(radioList))
                }
            }
    }
}