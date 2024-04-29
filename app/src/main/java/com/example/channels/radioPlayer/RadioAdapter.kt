package com.example.channels.radioPlayer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.channels.databinding.RadioViewBinding
import com.example.domain.model.Radio

class RadioAdapter(
    private val context: Context,
    private val radioList : MutableList<Radio>,
    private val itemClickListener: OnRadioItemClickListener
): RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {

    interface OnRadioItemClickListener{

        fun onRadioItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioAdapter.RadioViewHolder {
        val binding = RadioViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RadioViewHolder(binding)
    }

    inner class RadioViewHolder(private val binding: RadioViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(radioItem: Radio, context: Context){
            Glide.with(context)
                .load(radioItem.image)
                .transform(RoundedCorners(18))
                .into(binding.image)
            binding.radioName.text = radioItem.name
        }
    }

    override fun onBindViewHolder(holder: RadioAdapter.RadioViewHolder, position: Int) {
        val radioItem = radioList[position]
        holder.bind(radioItem, context)
        holder.itemView.setOnClickListener {
            itemClickListener.onRadioItemClicked(position)
        }
    }

    override fun getItemCount(): Int = radioList.size
}