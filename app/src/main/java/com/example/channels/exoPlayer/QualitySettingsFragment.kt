package com.example.channels.exoPlayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.R
import com.example.channels.databinding.FragmentQualitySettingsBinding

const val QUALITY_LIST_KEY = "QUALITY_LIST_KEY"
const val CURRENT_RESOLUTION_KEY = "CURRENT_RESOLUTION_KEY"
const val xLocation = 15
const val yLocation = 115

class QualitySettingsFragment : DialogFragment() {

    override fun getTheme() = R.style.RoundedCornersDialog
    private lateinit var binding: FragmentQualitySettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val location = WindowManager.LayoutParams()
        location.gravity = Gravity.BOTTOM or Gravity.END
        location.x = xLocation
        location.y = yLocation
        dialog!!.window?.attributes = location
        binding = FragmentQualitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qualityList = arguments?.getIntegerArrayList(QUALITY_LIST_KEY)
        val currentResolution = arguments?.getInt(CURRENT_RESOLUTION_KEY)
        var qualityArray = qualityList?.toTypedArray()
        qualityArray = qualityArray?.plus(autoQualityId)

        binding.qualityListView.adapter =
            qualityArray?.let { ListViewAdapter(requireContext(), it, currentResolution) }

        binding.qualityListView.setOnItemClickListener { _, _, i, _ ->
            val resultData = Bundle()
            qualityArray?.get(i)?.let { resultData.putInt("quality", it) }
            setFragmentResult(SET_RESULT, resultData)
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            qualityList: MutableList<Int>,
            currentResolution: Int,
        ): QualitySettingsFragment {
            return QualitySettingsFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(QUALITY_LIST_KEY, ArrayList(qualityList))
                    putInt(CURRENT_RESOLUTION_KEY, currentResolution)
                }
            }
        }
    }
}