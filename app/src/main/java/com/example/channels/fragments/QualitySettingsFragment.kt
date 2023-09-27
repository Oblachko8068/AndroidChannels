package com.example.channels.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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
        val padding10inDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics
        ).toInt()
        val textViews = mutableListOf<Button>()

        binding.auto.setBackgroundResource(R.color.icon_enable)
        textViews.add(binding.auto)
        binding.auto.setOnClickListener {
            it.setBackgroundResource(R.color.icon_enable)
            binding.auto.setTextColor(R.color.text_active)
            textViews.forEach {text ->
                text.setTextColor(Color.BLACK)
                text.setBackgroundResource(R.color.text_active)
            }
            val resultData = Bundle()
            resultData.putInt("quality", 0)
            setFragmentResult("result", resultData)
        }

        qualityList?.asReversed()?.forEach { quality ->
            val textView = Button(requireContext())
            textView.id = quality
            textView.textSize = 16F
            textView.text = "${quality}p"
            textView.setTextColor(R.color.tab_block_background)

            textView.setPadding(0, padding10inDp, 0, padding10inDp)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.setOnClickListener {
                textView.setBackgroundResource(R.color.icon_enable)
                textViews.forEach {
                    if (it.id != textView.id) {
                        it.setTextColor(Color.BLACK)
                        it.setBackgroundResource(R.color.text_active)
                    }
                }
                val resultData = Bundle()
                resultData.putInt("quality", quality)
                setFragmentResult("result", resultData)
            }
            textViews.add(textView)
            binding.container.addView(textView)
        }
    }

    companion object {
        fun newInstance(qualityList: MutableList<Int>, location: IntArray): QualitySettingsFragment {
            return QualitySettingsFragment().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList("qualityList", ArrayList(qualityList))
                    putIntArray("locationSettings", location)
                }
            }
        }
    }
}
/*val x = location?.get(0)
        val y = location?.get(1)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val layoutParams = dialog?.window?.attributes
        //layoutParams?.gravity = Gravity.BOTTOM or Gravity.END

        if (x != null) {
            layoutParams?.x = x - dialog?.window?.decorView?.width!!
        }
        if (y != null) {
            layoutParams?.y = y - 100
        }
        dialog?.window?.attributes = layoutParams*/