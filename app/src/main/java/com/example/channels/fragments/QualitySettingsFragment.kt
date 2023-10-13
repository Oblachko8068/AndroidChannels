package com.example.channels.fragments

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.R
import com.example.channels.databinding.FragmentQualitySettingsBinding


class QualitySettingsFragment : DialogFragment() {
    override fun getTheme() = R.style.RoundedCornersDialog

    private lateinit var binding: FragmentQualitySettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val location = WindowManager.LayoutParams()
        location.gravity = Gravity.BOTTOM or Gravity.END
        location.x = 15
        location.y = 115
        dialog!!.window?.attributes = location
        binding = FragmentQualitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qualityList = arguments?.getIntegerArrayList("qualityList")

        val currentResolution = arguments?.getInt("currentResolution")

        //качества
        qualityList?.forEach { quality ->
            val button = createButton(false, quality, currentResolution)
            binding.container.addView(button)
            val line = View(requireContext())
            line.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams(128.dp(), 1.dp())
            )
            binding.container.addView(line)
        }
        //Auto
        val button = createButton(true, 0, currentResolution)
        binding.container.addView(button)
    }
    private fun createButton(isAuto: Boolean, quality: Int, currentResolution: Int?): Button{
        val button = Button(requireContext())
        button.id = if (isAuto) -1 else quality
        button.textSize = 16F
        button.text = if (isAuto) "AUTO" else "${quality}p"
        if (isAuto){
            if (currentResolution == -1) {
                button.setBackgroundResource(R.color.played_video)
                button.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_active))
            } else {
                button.setBackgroundResource(R.color.text_dark)
                button.setTextColor(ContextCompat.getColor(requireContext(),R.color.unplayed_video_text_color))
            }
        } else {
            if (currentResolution == quality) {
                button.setBackgroundResource(R.color.played_video)
                button.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_active))
            } else {
                button.setBackgroundResource(R.color.text_dark)
                button.setTextColor(ContextCompat.getColor(requireContext(),R.color.unplayed_video_text_color))
            }
        }
        button.layoutParams = ViewGroup.LayoutParams(
            128.dp(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        button.setOnClickListener {
            val resultData = Bundle()
            resultData.putInt("quality", if (isAuto) -1 else quality)
            setFragmentResult("result", resultData)
            dismiss()
        }
        return button
    }
    companion object {
        fun newInstance(
            qualityList: MutableList<Int>,
            currentResolution: Int,
        ): QualitySettingsFragment {
            return QualitySettingsFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList("qualityList", ArrayList(qualityList))
                    putInt("currentResolution", currentResolution)
                }
            }
        }
    }
    private fun Int.dp(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }
}