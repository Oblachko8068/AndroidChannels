package com.example.channels.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.channels.R
import com.example.channels.databinding.FragmentQualitySettingsBinding


class QualitySettingsFragment : DialogFragment() {

    private lateinit var binding: FragmentQualitySettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQualitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qualityList = arguments?.getIntegerArrayList("qualityList")
        val location = arguments?.getIntArray("locationSettings")
        val currentResolution = arguments?.getInt("currentResolution")
        val padding10inDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
        ).toInt()
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 128f, resources.displayMetrics
        ).toInt()
        val buttonViews = mutableListOf<Button>()

        //фигня
        val x = location?.get(0)
        val y = location?.get(1)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val layoutParams = dialog?.window?.attributes
        //layoutParams?.gravity = Gravity.TOP or Gravity.START

        if (x != null) {
            layoutParams?.x = x - dialog?.window?.decorView?.width!!
        }
        if (y != null) {
            layoutParams?.y = y - 100
        }
        dialog?.window?.attributes = layoutParams

        //качества
        qualityList?.asReversed()?.forEach { quality ->
            val button = Button(requireContext())
            button.id = quality
            button.textSize = 16F
            button.text = "${quality}p"
            button.setPadding(0, padding10inDp, 0, padding10inDp)

            if (currentResolution == quality) {
                button.setBackgroundResource(R.color.icon_enable)
                button.setTextColor(R.color.text_active)
            } else {
                button.setBackgroundResource(R.color.text_dark)
                button.setTextColor(R.color.text_default)
            }
            button.setOnClickListener {
                val resultData = Bundle()
                resultData.putInt("quality", quality)
                setFragmentResult("result", resultData)
                dismiss()
            }
            buttonViews.add(button)
            binding.container.addView(button)
            val line = View(requireContext())
            line.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams(width, 1)
            )
            binding.container.addView(line)
        }

        //Auto
        val button = Button(requireContext())
        button.id = 0
        button.textSize = 16F
        button.text = "AUTO"
        if (currentResolution == 0) {
            button.setBackgroundResource(R.color.icon_enable)
            button.setTextColor(R.color.text_active)
        } else {
            button.setBackgroundResource(R.color.text_dark)
            button.setTextColor(R.color.text_default)
        }
        buttonViews.add(button)
        binding.container.addView(button)
        button.setOnClickListener {
            it.setBackgroundResource(R.color.icon_enable)
            buttonViews.forEach { text ->
                text.setTextColor(Color.BLACK)
                text.setBackgroundResource(R.color.text_active)
            }
            val resultData = Bundle()
            resultData.putInt("quality", 0)
            setFragmentResult("result", resultData)
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            qualityList: MutableList<Int>,
            location: IntArray,
            currentResolution: Int
        ): QualitySettingsFragment {
            return QualitySettingsFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList("qualityList", ArrayList(qualityList))
                    putIntArray("locationSettings", location)
                    putInt("currentResolution", currentResolution)
                }
            }
        }
    }
}