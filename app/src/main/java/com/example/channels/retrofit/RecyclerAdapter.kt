package com.example.channels.retrofit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.ChannelPlayer
import com.example.channels.R
import com.example.channels.databinding.ChannelBlockBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class RecyclerAdapter(
    private val context: Context,
    private var channelJSONList: List<ChannelJSON>,
    private val itemClickListener: OnChannelItemClickListener
) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    interface OnChannelItemClickListener {
        fun onChannelItemClicked(channel: ChannelJSON)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannelJSONS: List<ChannelJSON>) {
        channelJSONList = newChannelJSONS
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ChannelBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    class MyViewHolder(private val binding: ChannelBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChannelJSON, context: Context) {
            Picasso.get().load(item.image).into(binding.channelIcon)
            binding.channelName.text = item.name
            binding.channelDesc.text = item.epg[0].title
            binding.iconFav.setImageResource(R.drawable.baseline_star_24)
            binding.iconFav.setColorFilter(
                ContextCompat.getColor(context, R.color.icon_disable)
            )
            val intArray1 = getSavedNewIntArray(context)
            if (item.id in intArray1) {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_enable)
                )
            }
            binding.iconFav.setOnClickListener {
                var intArray = getSavedNewIntArray(context)
                if (item.id in intArray) {
                    binding.iconFav.setColorFilter(
                        ContextCompat.getColor(context, R.color.icon_disable)
                    )
                    for (i in intArray.indices) {
                        if (intArray[i] == item.id) {
                            intArray = removeElementFromArray(intArray, i)
                            break
                        }
                    }
                } else {
                    intArray = addElementToArray(intArray, item.id)
                    binding.iconFav.setColorFilter(
                        ContextCompat.getColor(context, R.color.icon_enable)
                    )
                }
                saveNewIntArray(context, intArray)
            }
        }

        private fun addElementToArray(array: IntArray, element: Int): IntArray {
            val newArray = IntArray(array.size + 1)
            array.copyInto(newArray)
            newArray[array.size] = element
            return newArray
        }

        private fun removeElementFromArray(array: IntArray, indexToRemove: Int): IntArray {
            return array.filterIndexed { index, _ -> index != indexToRemove }.toIntArray()
        }

        private fun getSavedNewIntArray(context: Context): IntArray {
            val sharedPref =
                context.getSharedPreferences("new_array_preferences", Context.MODE_PRIVATE)
            val jsonString = sharedPref.getString("new_int_array_data", null)

            return try {
                if (jsonString != null) {
                    Gson().fromJson(jsonString, IntArray::class.java)
                } else {
                    IntArray(0)
                }
            } catch (e: Exception) {
                IntArray(0)  // Возвращаем пустой (нулевой) массив в случае ошибки
            }
        }

        // Добавляем функцию для сохранения массива целочисленных значений в SharedPreferences
        private fun saveNewIntArray(context: Context, intArray: IntArray) {
            val sharedPref =
                context.getSharedPreferences("new_array_preferences", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val jsonString = Gson().toJson(intArray)
            editor.putString("new_int_array_data", jsonString)
            editor.apply()
        }
    }

    override fun getItemCount() = channelJSONList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val channel = channelJSONList[position]
        holder.bind(channel, context)

        holder.itemView.setOnClickListener {
            itemClickListener.onChannelItemClicked(channel)
        }
    }
}