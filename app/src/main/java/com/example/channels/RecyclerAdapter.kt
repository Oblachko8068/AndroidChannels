package com.example.channels

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.channels.databinding.ChannelBlockBinding
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg
import com.google.gson.Gson


class RecyclerAdapter(
    private val context: Context,
    private var channel: List<Channel>,
    private var epg: List<Epg>,
    private val itemClickListener: OnChannelItemClickListener
) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    interface OnChannelItemClickListener {
        fun onChannelItemClicked(channel: Channel, epg: Epg)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannel: List<Channel>) {
        channel = newChannel
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ChannelBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    class MyViewHolder(private val binding: ChannelBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(channelItem: Channel, epgItem: Epg, context: Context) {
            Glide.with(context)
                .load(channelItem.image)
                .into(binding.channelIcon)
            binding.channelName.text = channelItem.name
            binding.channelDesc.text = epgItem.title
            binding.iconFav.setImageResource(R.drawable.baseline_star_24)
            binding.iconFav.setColorFilter(
                ContextCompat.getColor(context, R.color.icon_disable)
            )
            val intArray1 = getSavedNewIntArray(context)
            if (channelItem.id in intArray1) {
                binding.iconFav.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_enable)
                )
            }
            binding.iconFav.setOnClickListener {
                var intArray = getSavedNewIntArray(context)
                if (channelItem.id in intArray) {
                    binding.iconFav.setColorFilter(
                        ContextCompat.getColor(context, R.color.icon_disable)
                    )
                    for (i in intArray.indices) {
                        if (intArray[i] == channelItem.id) {
                            intArray = removeElementFromArray(intArray, i)
                            break
                        }
                    }
                } else {
                    intArray = addElementToArray(intArray, channelItem.id)
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

    override fun getItemCount() = channel.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val channel = channel[position]
        val epg = epg.find { it.channelID == channel.id }
        if (epg != null) {
            holder.bind(channel, epg, context)
        }

        holder.itemView.setOnClickListener {
            if (epg != null) {
                itemClickListener.onChannelItemClicked(channel, epg)
            }
        }
    }
}