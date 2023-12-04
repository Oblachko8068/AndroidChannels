package com.example.channels.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.channels.R
import com.example.channels.databinding.ListViewItemBinding

class ListViewAdapter(
    private val context: Context,
    private val qualityArray: Array<Int>,
    private val currentResolution: Int?
) :
    ArrayAdapter<Int>(context, R.layout.list_view_item, qualityArray) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = ListViewItemBinding.inflate(inflater)
        binding.listItemText.text =
            if (qualityArray[position] == -1) "AUTO" else "${qualityArray[position]}p"

        if (qualityArray[position] == currentResolution) {
            binding.listItemText.setBackgroundResource(R.color.played_video)
            binding.listItemText.setTextColor(ContextCompat.getColor(context, R.color.text_active))
        } else {
            binding.listItemText.setBackgroundResource(R.color.text_dark)
            binding.listItemText.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.unplayed_video_text_color
                )
            )
        }

        return binding.root
    }
}